package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.WithCurrentCCAdministrator;
import com.thirdcc.webapp.annotations.authorization.WithEventHead;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.domain.Claim;
import com.thirdcc.webapp.repository.ClaimRepository;
import com.thirdcc.webapp.service.dto.ClaimDTO;
import com.thirdcc.webapp.service.mapper.ClaimMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thirdcc.webapp.domain.enumeration.ClaimStatus;
import org.junit.jupiter.api.AfterEach;
/**
 * Integration tests for the {@Link ClaimResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@InitYearSession
public class ClaimResourceIT {

    private static final Long DEFAULT_RECEIPT_ID = 1L;
    private static final Long UPDATED_RECEIPT_ID = 2L;

    private static final Long DEFAULT_TRANSACTION_ID = 1L;
    private static final Long UPDATED_TRANSACTION_ID = 2L;

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(1, 2);
    private static final BigDecimal UPDATED_AMOUNT = BigDecimal.valueOf(2, 2);

    private static final ClaimStatus DEFAULT_STATUS = ClaimStatus.OPEN;
    private static final ClaimStatus UPDATED_STATUS = ClaimStatus.CLAIMED;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ClaimMapper claimMapper;

    @Autowired
    private MockMvc restClaimMockMvc;

    private Claim claim;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Claim createEntity() {
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
    public static Claim createUpdatedEntity() {
        Claim claim = new Claim()
            .receiptId(UPDATED_RECEIPT_ID)
            .transactionId(UPDATED_TRANSACTION_ID)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS);
        return claim;
    }

    @BeforeEach
    public void initTest() {
        claim = createEntity();
    }

    @AfterEach
    public void cleanUp() {
        claimRepository.deleteAll();
    }
    
    private Claim initClaimDB() {
        return claimRepository.saveAndFlush(claim);
    }
    
    @Test
    @WithCurrentCCAdministrator
    public void getAllOpenClaims_UserWithRoleAdmin() throws Exception {
        // Initialize the database
        Claim savedClaim = initClaimDB();

        // Get all the claim in the event
        restClaimMockMvc.perform(get("/api/claims/"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedClaim.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptId").value(hasItem(DEFAULT_RECEIPT_ID.intValue())))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID.intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @WithCurrentCCAdministrator
    public void getAllOpenClaims_UserWithRoleAdmin_NoClaimWithOpenStatus() throws Exception {
        // Initialize the database
        claim.setStatus(UPDATED_STATUS);
        initClaimDB();

        // Get all the claim in the event
        restClaimMockMvc.perform(get("/api/claims?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isEmpty());
    }
    
    @Test
    @WithEventHead
    public void getAllOpenClaims_UserIsNotAdmin_ShouldThrow403IsForbidden() throws Exception {
        // Initialize the database
        initClaimDB();
        
        // Get all the claim in the event
        restClaimMockMvc.perform(get("/api/claims?sort=id,desc"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateClaimStatus_UserWithRoleAdmin() throws Exception {
        // Initialize the database
        initClaimDB();

        int databaseSizeBeforeUpdate = claimRepository.findAll().size();

        restClaimMockMvc.perform(put("/api/claims/{id}/status/{claimStatus}", claim.getId(), UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the Checklist in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeUpdate);
        Claim testClaim = claimList.get(claimList.size() - 1);
        assertThat(testClaim.getReceiptId()).isEqualTo(DEFAULT_RECEIPT_ID);
        assertThat(testClaim.getTransactionId()).isEqualTo(DEFAULT_TRANSACTION_ID);
        assertThat(testClaim.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testClaim.getStatus()).isEqualTo(UPDATED_STATUS);
    }
    
    @Test
    @WithEventHead
    public void updateClaimStatus_UserIsNotAdmin_ShouldThrow403IsForbidden() throws Exception {
        // Initialize the database
        initClaimDB();

        int databaseSizeBeforeUpdate = claimRepository.findAll().size();

        restClaimMockMvc.perform(put("/api/claims/{id}/status/{claimStatus}", claim.getId(), UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        // Validate the Checklist in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeUpdate);
        Claim testClaim = claimList.get(claimList.size() - 1);
        assertThat(testClaim.getReceiptId()).isEqualTo(DEFAULT_RECEIPT_ID);
        assertThat(testClaim.getTransactionId()).isEqualTo(DEFAULT_TRANSACTION_ID);
        assertThat(testClaim.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testClaim.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateClaimStatus_ClaimIsNotExists_ShouldThrow400BadRequest() throws Exception {
        // Initialize the database
        initClaimDB();

        int databaseSizeBeforeUpdate = claimRepository.findAll().size();

        restClaimMockMvc.perform(put("/api/claims/{id}/status/{claimStatus}", Long.MAX_VALUE, UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateClaimStatus_ClaimIsNotOpen_ShouldThrow400BadRequest() throws Exception {
        // Initialize the database
        claim.setStatus(UPDATED_STATUS);
        initClaimDB();

        int databaseSizeBeforeUpdate = claimRepository.findAll().size();

        restClaimMockMvc.perform(put("/api/claims/{id}/status/{claimStatus}", claim.getId(), UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeUpdate);
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
