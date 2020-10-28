package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Claim;
import com.thirdcc.webapp.repository.ClaimRepository;
import com.thirdcc.webapp.service.ClaimService;
import com.thirdcc.webapp.service.dto.ClaimDTO;
import com.thirdcc.webapp.service.mapper.ClaimMapper;
import com.thirdcc.webapp.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.thirdcc.webapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thirdcc.webapp.domain.enumeration.ClaimStatus;
/**
 * Integration tests for the {@Link ClaimResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
public class ClaimResourceIT {

    private static final Long DEFAULT_RECEIPT_ID = 1L;
    private static final Long UPDATED_RECEIPT_ID = 2L;

    private static final Long DEFAULT_TRANSACTION_ID = 1L;
    private static final Long UPDATED_TRANSACTION_ID = 2L;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final ClaimStatus DEFAULT_STATUS = ClaimStatus.OPEN;
    private static final ClaimStatus UPDATED_STATUS = ClaimStatus.CLAIMED;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ClaimMapper claimMapper;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restClaimMockMvc;

    private Claim claim;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ClaimResource claimResource = new ClaimResource(claimService);
        this.restClaimMockMvc = MockMvcBuilders.standaloneSetup(claimResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Claim createEntity(EntityManager em) {
        Claim claim = new Claim()
            .receiptId(DEFAULT_RECEIPT_ID)
            .transactionId(DEFAULT_TRANSACTION_ID)
            .amount(DEFAULT_AMOUNT)
            .status(DEFAULT_STATUS);
        return claim;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Claim createUpdatedEntity(EntityManager em) {
        Claim claim = new Claim()
            .receiptId(UPDATED_RECEIPT_ID)
            .transactionId(UPDATED_TRANSACTION_ID)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS);
        return claim;
    }

    @BeforeEach
    public void initTest() {
        claim = createEntity(em);
    }

    @Test
    @Transactional
    public void createClaim() throws Exception {
        int databaseSizeBeforeCreate = claimRepository.findAll().size();

        // Create the Claim
        ClaimDTO claimDTO = claimMapper.toDto(claim);
        restClaimMockMvc.perform(post("/api/claims")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(claimDTO)))
            .andExpect(status().isCreated());

        // Validate the Claim in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeCreate + 1);
        Claim testClaim = claimList.get(claimList.size() - 1);
        assertThat(testClaim.getReceiptId()).isEqualTo(DEFAULT_RECEIPT_ID);
        assertThat(testClaim.getTransactionId()).isEqualTo(DEFAULT_TRANSACTION_ID);
        assertThat(testClaim.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testClaim.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createClaimWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = claimRepository.findAll().size();

        // Create the Claim with an existing ID
        claim.setId(1L);
        ClaimDTO claimDTO = claimMapper.toDto(claim);

        // An entity with an existing ID cannot be created, so this API call must fail
        restClaimMockMvc.perform(post("/api/claims")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(claimDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Claim in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllClaims() throws Exception {
        // Initialize the database
        claimRepository.saveAndFlush(claim);

        // Get all the claimList
        restClaimMockMvc.perform(get("/api/claims?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(claim.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptId").value(hasItem(DEFAULT_RECEIPT_ID.intValue())))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID.intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getClaim() throws Exception {
        // Initialize the database
        claimRepository.saveAndFlush(claim);

        // Get the claim
        restClaimMockMvc.perform(get("/api/claims/{id}", claim.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(claim.getId().intValue()))
            .andExpect(jsonPath("$.receiptId").value(DEFAULT_RECEIPT_ID.intValue()))
            .andExpect(jsonPath("$.transactionId").value(DEFAULT_TRANSACTION_ID.intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingClaim() throws Exception {
        // Get the claim
        restClaimMockMvc.perform(get("/api/claims/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClaim() throws Exception {
        // Initialize the database
        claimRepository.saveAndFlush(claim);

        int databaseSizeBeforeUpdate = claimRepository.findAll().size();

        // Update the claim
        Claim updatedClaim = claimRepository.findById(claim.getId()).get();
        // Disconnect from session so that the updates on updatedClaim are not directly saved in db
        em.detach(updatedClaim);
        updatedClaim
            .receiptId(UPDATED_RECEIPT_ID)
            .transactionId(UPDATED_TRANSACTION_ID)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS);
        ClaimDTO claimDTO = claimMapper.toDto(updatedClaim);

        restClaimMockMvc.perform(put("/api/claims")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(claimDTO)))
            .andExpect(status().isOk());

        // Validate the Claim in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeUpdate);
        Claim testClaim = claimList.get(claimList.size() - 1);
        assertThat(testClaim.getReceiptId()).isEqualTo(UPDATED_RECEIPT_ID);
        assertThat(testClaim.getTransactionId()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testClaim.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testClaim.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingClaim() throws Exception {
        int databaseSizeBeforeUpdate = claimRepository.findAll().size();

        // Create the Claim
        ClaimDTO claimDTO = claimMapper.toDto(claim);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClaimMockMvc.perform(put("/api/claims")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(claimDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Claim in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteClaim() throws Exception {
        // Initialize the database
        claimRepository.saveAndFlush(claim);

        int databaseSizeBeforeDelete = claimRepository.findAll().size();

        // Delete the claim
        restClaimMockMvc.perform(delete("/api/claims/{id}", claim.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Claim.class);
        Claim claim1 = new Claim();
        claim1.setId(1L);
        Claim claim2 = new Claim();
        claim2.setId(claim1.getId());
        assertThat(claim1).isEqualTo(claim2);
        claim2.setId(2L);
        assertThat(claim1).isNotEqualTo(claim2);
        claim1.setId(null);
        assertThat(claim1).isNotEqualTo(claim2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClaimDTO.class);
        ClaimDTO claimDTO1 = new ClaimDTO();
        claimDTO1.setId(1L);
        ClaimDTO claimDTO2 = new ClaimDTO();
        assertThat(claimDTO1).isNotEqualTo(claimDTO2);
        claimDTO2.setId(claimDTO1.getId());
        assertThat(claimDTO1).isEqualTo(claimDTO2);
        claimDTO2.setId(2L);
        assertThat(claimDTO1).isNotEqualTo(claimDTO2);
        claimDTO1.setId(null);
        assertThat(claimDTO1).isNotEqualTo(claimDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(claimMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(claimMapper.fromId(null)).isNull();
    }
}
