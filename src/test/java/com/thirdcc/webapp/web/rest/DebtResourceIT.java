package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Debt;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.repository.DebtRepository;
import com.thirdcc.webapp.service.DebtService;
import com.thirdcc.webapp.service.dto.DebtDTO;
import com.thirdcc.webapp.service.mapper.DebtMapper;
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

import com.thirdcc.webapp.domain.enumeration.DebtStatus;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.repository.EventRepository;
import org.junit.jupiter.api.AfterEach;
/**
 * Integration tests for the {@Link DebtResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
public class DebtResourceIT {

    private static final Long DEFAULT_RECEIPT_ID = 1L;
    private static final Long UPDATED_RECEIPT_ID = 2L;

    private static final Long DEFAULT_EVENT_ATTENDEE_ID = 1L;
    private static final Long UPDATED_EVENT_ATTENDEE_ID = 2L;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final DebtStatus DEFAULT_STATUS = DebtStatus.OPEN;
    private static final DebtStatus UPDATED_STATUS = DebtStatus.COLLECTED;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String DEFAULT_EVENT_REMARKS = "DEFAULT_REMARKS";
    private static final String DEFAULT_EVENT_VENUE = "DEFAULT_VENUE";
    private static final Instant DEFAULT_EVENT_START_DATE = Instant.now().plus(1, ChronoUnit.DAYS);
    private static final Instant DEFAULT_EVENT_END_DATE = Instant.now().plus(2, ChronoUnit.DAYS);
    private static final EventStatus DEFAULT_EVENT_STATUS = EventStatus.OPEN;
    private static final BigDecimal DEFAULT_EVENT_FEE = BigDecimal.valueOf(10.0);

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private DebtMapper debtMapper;

    @Autowired
    private DebtService debtService;

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

    private MockMvc restDebtMockMvc;

    private Debt debt;
    private Event event;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DebtResource debtResource = new DebtResource(debtService);
        this.restDebtMockMvc = MockMvcBuilders.standaloneSetup(debtResource)
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
    public static Debt createEntity(EntityManager em) {
        return new Debt()
            .receiptId(DEFAULT_RECEIPT_ID)
            .eventAttendeeId(DEFAULT_EVENT_ATTENDEE_ID)
            .amount(DEFAULT_AMOUNT)
            .status(DEFAULT_STATUS);
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Debt createUpdatedEntity(EntityManager em) {
        return new Debt()
            .receiptId(UPDATED_RECEIPT_ID)
            .eventAttendeeId(UPDATED_EVENT_ATTENDEE_ID)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS);
    }
    
    public static Event createEventEntity() {
        return new Event()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .remarks(DEFAULT_EVENT_REMARKS)
            .startDate(DEFAULT_EVENT_START_DATE)
            .endDate(DEFAULT_EVENT_END_DATE)
            .status(DEFAULT_EVENT_STATUS)
            .venue(DEFAULT_EVENT_VENUE)
            .fee(DEFAULT_EVENT_FEE);
    }

    @BeforeEach
    public void initTest() {
        debt = createEntity(em);
    }

    @AfterEach
    public void cleanUp() {
        debtRepository.deleteAll();
    }
    
    private void initDebtDB() {
        debtRepository.saveAndFlush(debt);
    }
    
    private void initEventDB() {
        eventRepository.saveAndFlush(event);
    }

    @Test
    @Transactional
    public void createDebt() throws Exception {
        initDebtDB();
        
        int databaseSizeBeforeCreate = debtRepository.findAll().size();

        // Create the Debt
        DebtDTO debtDTO = debtMapper.toDto(debt);
        restDebtMockMvc.perform(post("/api/debts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(debtDTO)))
            .andExpect(status().isCreated());

        // Validate the Debt in the database
        List<Debt> debtList = debtRepository.findAll();
        assertThat(debtList).hasSize(databaseSizeBeforeCreate + 1);
        Debt testDebt = debtList.get(debtList.size() - 1);
        assertThat(testDebt.getReceiptId()).isEqualTo(DEFAULT_RECEIPT_ID);
        assertThat(testDebt.getEventAttendeeId()).isEqualTo(DEFAULT_EVENT_ATTENDEE_ID);
        assertThat(testDebt.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testDebt.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createDebtWithExistingId() throws Exception {
        initDebtDB();
        
        int databaseSizeBeforeCreate = debtRepository.findAll().size();

        // Create the Debt with an existing ID
        debt.setId(1L);
        DebtDTO debtDTO = debtMapper.toDto(debt);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDebtMockMvc.perform(post("/api/debts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(debtDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Debt in the database
        List<Debt> debtList = debtRepository.findAll();
        assertThat(debtList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllDebts() throws Exception {
        initDebtDB();
        
        // Initialize the database
        debtRepository.saveAndFlush(debt);

        // Get all the debtList
        restDebtMockMvc.perform(get("/api/debts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(debt.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptId").value(hasItem(DEFAULT_RECEIPT_ID.intValue())))
            .andExpect(jsonPath("$.[*].eventAttendeeId").value(hasItem(DEFAULT_EVENT_ATTENDEE_ID.intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    public void getAllDebtsByEventId() throws Exception {
        //TODO implement checking for admin and event crew
        // Initialize the database
        initDebtDB();
        initEventDB();

        // Get all the debtList
        restDebtMockMvc.perform(get("/api/debts/event/{eventId}?sort=id,desc", event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(debt.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptId").value(hasItem(DEFAULT_RECEIPT_ID.intValue())))
            .andExpect(jsonPath("$.[*].eventAttendeeId").value(hasItem(DEFAULT_EVENT_ATTENDEE_ID.intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getDebt() throws Exception {
        initDebtDB();
        
        // Initialize the database
        debtRepository.saveAndFlush(debt);

        // Get the debt
        restDebtMockMvc.perform(get("/api/debts/{id}", debt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(debt.getId().intValue()))
            .andExpect(jsonPath("$.receiptId").value(DEFAULT_RECEIPT_ID.intValue()))
            .andExpect(jsonPath("$.eventAttendeeId").value(DEFAULT_EVENT_ATTENDEE_ID.intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDebt() throws Exception {
        initDebtDB();
        
        // Get the debt
        restDebtMockMvc.perform(get("/api/debts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDebt() throws Exception {
        initDebtDB();
        
        // Initialize the database
        debtRepository.saveAndFlush(debt);

        int databaseSizeBeforeUpdate = debtRepository.findAll().size();

        // Update the debt
        Debt updatedDebt = debtRepository.findById(debt.getId()).get();
        // Disconnect from session so that the updates on updatedDebt are not directly saved in db
        em.detach(updatedDebt);
        updatedDebt
            .receiptId(UPDATED_RECEIPT_ID)
            .eventAttendeeId(UPDATED_EVENT_ATTENDEE_ID)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS);
        DebtDTO debtDTO = debtMapper.toDto(updatedDebt);

        restDebtMockMvc.perform(put("/api/debts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(debtDTO)))
            .andExpect(status().isOk());

        // Validate the Debt in the database
        List<Debt> debtList = debtRepository.findAll();
        assertThat(debtList).hasSize(databaseSizeBeforeUpdate);
        Debt testDebt = debtList.get(debtList.size() - 1);
        assertThat(testDebt.getReceiptId()).isEqualTo(UPDATED_RECEIPT_ID);
        assertThat(testDebt.getEventAttendeeId()).isEqualTo(UPDATED_EVENT_ATTENDEE_ID);
        assertThat(testDebt.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testDebt.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingDebt() throws Exception {
        initDebtDB();
        
        int databaseSizeBeforeUpdate = debtRepository.findAll().size();

        // Create the Debt
        DebtDTO debtDTO = debtMapper.toDto(debt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDebtMockMvc.perform(put("/api/debts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(debtDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Debt in the database
        List<Debt> debtList = debtRepository.findAll();
        assertThat(debtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteDebt() throws Exception {
        initDebtDB();
        
        // Initialize the database
        debtRepository.saveAndFlush(debt);

        int databaseSizeBeforeDelete = debtRepository.findAll().size();

        // Delete the debt
        restDebtMockMvc.perform(delete("/api/debts/{id}", debt.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Debt> debtList = debtRepository.findAll();
        assertThat(debtList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Debt.class);
        Debt debt1 = new Debt();
        debt1.setId(1L);
        Debt debt2 = new Debt();
        debt2.setId(debt1.getId());
        assertThat(debt1).isEqualTo(debt2);
        debt2.setId(2L);
        assertThat(debt1).isNotEqualTo(debt2);
        debt1.setId(null);
        assertThat(debt1).isNotEqualTo(debt2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DebtDTO.class);
        DebtDTO debtDTO1 = new DebtDTO();
        debtDTO1.setId(1L);
        DebtDTO debtDTO2 = new DebtDTO();
        assertThat(debtDTO1).isNotEqualTo(debtDTO2);
        debtDTO2.setId(debtDTO1.getId());
        assertThat(debtDTO1).isEqualTo(debtDTO2);
        debtDTO2.setId(2L);
        assertThat(debtDTO1).isNotEqualTo(debtDTO2);
        debtDTO1.setId(null);
        assertThat(debtDTO1).isNotEqualTo(debtDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(debtMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(debtMapper.fromId(null)).isNull();
    }
}
