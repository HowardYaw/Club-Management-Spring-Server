package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.WithCurrentCCAdministrator;
import com.thirdcc.webapp.annotations.authorization.WithEventCrew;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.domain.enumeration.*;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.*;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.UserService;
import com.thirdcc.webapp.service.dto.EventBudgetTotalDTO;
import com.thirdcc.webapp.service.dto.ReceiptDTO;
import com.thirdcc.webapp.service.dto.TransactionDTO;
import com.thirdcc.webapp.service.mapper.TransactionMapper;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
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

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.thirdcc.webapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link TransactionResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(username = TransactionResourceIT.USERNAME, roles = "ADMIN")
@InitYearSession
public class TransactionResourceIT {

    public static final String USERNAME = "admin";
    private static final String ENTITY_API_URL = "/api/transactions";

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long SMALLER_EVENT_ID = DEFAULT_EVENT_ID - 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final Long DEFAULT_RECEIPT_ID = 1L;
    private static final Long SMALLER_RECEIPT_ID = DEFAULT_RECEIPT_ID - 1L;
    private static final Long UPDATED_RECEIPT_ID = 2L;

    private static final TransactionType DEFAULT_TYPE = TransactionType.INCOME;
    private static final TransactionType UPDATED_TYPE = TransactionType.EXPENSE;

    private static final String DEFAULT_TITLE = "DEFAULT_TITLE";
    private static final String UPDATED_TITLE = "UPDATED_TITLE";

    private static final Instant DEFAULT_TRANSACTION_DATE = Instant.ofEpochMilli(4545);
    private static final Instant UPDATED_TRANSACTION_DATE = Instant.ofEpochMilli(8745);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_AMOUNT = DEFAULT_AMOUNT.subtract(BigDecimal.ONE);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_DETAILS = "TRANSACTION_DETAILS";
    private static final String UPDATED_DETAILS = "TRANSACTION_UPDATED_DETAILS";

    private static final String DEFAULT_IMAGE_LINK = "DEFAULT_IMAGE_LINK";
    private static final String UPDATED_IMAGE_LINK = "UPDATED_IMAGE_LINK";

    private static final TransactionStatus DEFAULT_TRANSACTION_STATUS = TransactionStatus.SUCCESS;

    private static final String DEFAULT_CLOSED_BY = USERNAME;
    private static final String UPDATED_CLOSED_BY = "UPDATED_CLOSED_BY";

    private static final String DEFAULT_CREATED_BY = USERNAME;
    private static final String UPDATED_CREATED_BY = "UPDATED_CREATED_BY";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochSecond(53565);
    private static final Instant UPDATED_CREATED_DATE = Instant.ofEpochSecond(63565);

    private static final String DEFAULT_LAST_MODIFIED_BY = USERNAME;
    private static final String UPDATED_LAST_MODIFIED_BY = "UPDATED_LAST_MODIFIED_BY";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochSecond(753565);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.ofEpochSecond(953565);

    // Event Default data
    private static final String DEFAULT_EVENT_NAME = "DEFAULT_EVENT_NAME";
    private static final String DEFAULT_EVENT_DESCRIPTION = "DEFAULT_EVENT_DESCRIPTION";
    private static final String DEFAULT_EVENT_REMARKS = "DEFAULT_EVENT_REMARKS";
    private static final String DEFAULT_EVENT_VENUE = "DEFAULT_EVENT_VENUE";
    private static final Instant DEFAULT_EVENT_START_DATE = Instant.now().minus(5, ChronoUnit.DAYS);
    private static final Instant DEFAULT_EVENT_END_DATE = Instant.now().plus(5, ChronoUnit.DAYS);
    private static final BigDecimal DEFAULT_EVENT_FEE = new BigDecimal(2123);
    private static final EventStatus DEFAULT_EVENT_STATUS = EventStatus.OPEN;
    private static final String DEFAULT_RECEIPT_IMAGE_TYPE = "DEFAULT_RECEIPT_IMAGE_TYPE";
    private static final String DEFAULT_RECEIPT_IMAGE_FILENAME = "DEFAULT_RECEIPT_IMAGE_FILENAME";
    private static final String DEFAULT_RECEIPT_IMAGE_CONTENT = "DEAFULT_RECEIPT_IMAGE_CONTENT";

    // Event Crew Default data
    private static final Long DEFAULT_USER_ID = 1L;
    private static final EventCrewRole EVENT_CREW_ROLE_HEAD = EventCrewRole.HEAD;


    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private User currentUser;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    @Autowired
    private EventRepository eventRepository;

    private Event event;

    @Autowired
    private ReceiptRepository receiptRepository;

    private ReceiptDTO receiptDTO;

    @Autowired
    private EventCrewRepository eventCrewRepository;

    private EventCrew eventCrew;

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
    public static Transaction createTransactionEntity() {
        return new Transaction()
            .title(DEFAULT_TITLE)
            .transactionDate(DEFAULT_TRANSACTION_DATE)
            .transactionType(DEFAULT_TYPE)
            .transactionStatus(DEFAULT_TRANSACTION_STATUS)
            .eventId(DEFAULT_EVENT_ID)
            .transactionAmount(DEFAULT_AMOUNT)
            .imageLink(DEFAULT_IMAGE_LINK)
            .closedBy(DEFAULT_CLOSED_BY)
            .description(DEFAULT_DETAILS);
    }

    public static Event createEventEntity() {
        return new Event()
            .name(DEFAULT_EVENT_NAME)
            .description(DEFAULT_EVENT_DESCRIPTION)
            .remarks(DEFAULT_EVENT_REMARKS)
            .startDate(DEFAULT_EVENT_START_DATE)
            .endDate(DEFAULT_EVENT_END_DATE)
            .fee(DEFAULT_EVENT_FEE)
            .status(DEFAULT_EVENT_STATUS)
            .venue(DEFAULT_EVENT_VENUE);
    }

    public static ReceiptDTO createReceiptDTO() {
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setFileName(DEFAULT_RECEIPT_IMAGE_FILENAME);
        receiptDTO.setFileType(DEFAULT_RECEIPT_IMAGE_TYPE);
        receiptDTO.setReceiptContent(DEFAULT_RECEIPT_IMAGE_CONTENT);
        return receiptDTO;
    }

    public static EventCrew createEventCrew() {
        EventCrew eventCrew = new EventCrew();
        eventCrew.setEventId(DEFAULT_EVENT_ID);
        eventCrew.setUserId(DEFAULT_USER_ID);
        return eventCrew;
    }

    @BeforeEach
    public void initTest() {
        transaction = createTransactionEntity();
        event = createEventEntity();
        receiptDTO = createReceiptDTO();
        eventCrew = createEventCrew();
    }

    @AfterEach
    public void cleanUp() {
        transactionRepository.deleteAll();
        eventRepository.deleteAll();
        receiptRepository.deleteAll();
        eventCrewRepository.deleteAll();
    }

//    @Test
    public void createTransaction_UserWithRoleAdmin() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionType(TransactionType.INCOME);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        int receiptDatabaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isCreated());

        // Validate Receipt Table
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(receiptDatabaseSizeBeforeCreate);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testTransaction.getTransactionType()).isEqualTo(TransactionType.INCOME);
        assertThat(testTransaction.getTransactionAmount()).isEqualTo(DEFAULT_AMOUNT.setScale(2));
        assertThat(testTransaction.getDescription()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testTransaction.getTransactionStatus()).isEqualTo(DEFAULT_TRANSACTION_STATUS);
    }

//    @Test
    @WithMockUser
    public void createTransaction_UserIsEventCrew() throws Exception {
        currentUser = getLoggedInUser();
        // Initialize the database
        Event savedEvent = initEventDB();
        eventCrew.setUserId(currentUser.getId());
        eventCrew.setEventId(savedEvent.getId());
        EventCrew savedEventCrew = initEventCrewDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionType(TransactionType.INCOME);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        int receiptDatabaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isCreated());

        // Validate Receipt Table
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(receiptDatabaseSizeBeforeCreate);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testTransaction.getTransactionType()).isEqualTo(TransactionType.INCOME);
        assertThat(testTransaction.getTransactionAmount()).isEqualTo(DEFAULT_AMOUNT.setScale(2));
        assertThat(testTransaction.getDescription()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testTransaction.getTransactionStatus()).isEqualTo(DEFAULT_TRANSACTION_STATUS);
    }

    @Test
    @WithMockUser
    public void createTransaction_UserNotEventCrewAndRoleUserOnly_ShouldThrow400() throws Exception {
        currentUser = getLoggedInUser();
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionType(TransactionType.INCOME);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        int receiptDatabaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isForbidden());

        // Validate Receipt Table
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(receiptDatabaseSizeBeforeCreate);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

//    @Test
    public void createTransaction_TypeExpense() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionType(TransactionType.EXPENSE);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        int receiptDatabaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        transactionDTO.setReceiptDTO(receiptDTO);
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isCreated());

        // Validate Receipt Table
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(receiptDatabaseSizeBeforeCreate + 1);
        Receipt testReceipt = receiptList.get(receiptList.size() - 1);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testTransaction.getTransactionType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(testTransaction.getTransactionAmount()).isEqualTo(DEFAULT_AMOUNT.setScale(2));
        assertThat(testTransaction.getDescription()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testTransaction.getTransactionStatus()).isEqualTo(DEFAULT_TRANSACTION_STATUS);
    }

    @Test
    public void createTransaction_TypeExpense_WithCancelledEvent_ShouldThrow400() throws Exception {
        // Initialize the database
        event.setStatus(EventStatus.CANCELLED);
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionType(TransactionType.EXPENSE);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        int receiptDatabaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        transactionDTO.setReceiptDTO(receiptDTO);
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate Receipt Table
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(receiptDatabaseSizeBeforeCreate);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void createTransaction_TypeExpense_WithNullEventId_ShouldThrow400() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(null);
        transaction.setTransactionType(TransactionType.EXPENSE);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        int receiptDatabaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        transactionDTO.setReceiptDTO(receiptDTO);
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate Receipt Table
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(receiptDatabaseSizeBeforeCreate);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

//    @Test
    public void createTransaction_TypeExpense_WithoutReceiptDTO_ShouldThrow400() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionType(TransactionType.EXPENSE);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        int receiptDatabaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate Receipt Table
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(receiptDatabaseSizeBeforeCreate);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

//    @Test
    public void createTransaction_TypeIncome() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionType(TransactionType.INCOME);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        int receiptDatabaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isCreated());

        // Validate Receipt Table
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(receiptDatabaseSizeBeforeCreate);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testTransaction.getTransactionType()).isEqualTo(TransactionType.INCOME);
        assertThat(testTransaction.getTransactionAmount()).isEqualTo(DEFAULT_AMOUNT.setScale(2));
        assertThat(testTransaction.getDescription()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testTransaction.getTransactionStatus()).isEqualTo(DEFAULT_TRANSACTION_STATUS);
    }

    @Test
    public void createTransaction_TypeIncome_WithCancelledEvent_ShouldThrow400() throws Exception {
        // Initialize the database
        event.setStatus(EventStatus.CANCELLED);
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionType(TransactionType.INCOME);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        int receiptDatabaseSizeBeforeCreate = receiptRepository.findAll().size();

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate Receipt Table
        List<Receipt> receiptList = receiptRepository.findAll();
        assertThat(receiptList).hasSize(receiptDatabaseSizeBeforeCreate);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

//    @Test
    public void createTransaction_WithExistingId_ShouldThrow400() throws Exception {
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        Transaction savedTransaction = transactionRepository.save(transaction);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction with an existing ID
        transaction.setId(savedTransaction.getId());
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc.perform(post("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }


//    @Test
    public void getAllTransactions() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        Transaction savedTransaction = initTransactionDB();

        // Get all the transactionList
        restTransactionMockMvc.perform(get("/api/transactions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(savedTransaction.getEventId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(savedTransaction.getTransactionType().toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(savedTransaction.getTransactionAmount().doubleValue())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(savedTransaction.getDescription())));
    }

//    @Test
    public void getAllTransactionByEventId() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        Transaction savedTransaction = initTransactionDB();

        // Get all the transactionList
        restTransactionMockMvc.perform(get("/api/transactions/event/{eventId}?sort=id,desc", savedEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(savedTransaction.getEventId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(savedTransaction.getTransactionType().toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(savedTransaction.getTransactionAmount().doubleValue())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(savedTransaction.getDescription())));
    }

//    @Test
    public void getAllTransactionByEventId_WithCancelledEvent_ShouldThrow400() throws Exception {
        // Initialize the database
        event.setStatus(EventStatus.CANCELLED);
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        Transaction savedTransaction = initTransactionDB();

        // Get all the transactionList
        restTransactionMockMvc.perform(get("/api/transactions/event/{eventId}?sort=id,desc", savedEvent.getId()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void getTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        Long id = transaction.getId();

        defaultTransactionShouldBeFound("id.equals=" + id);
        defaultTransactionShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionsByEventIdIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where eventId equals to DEFAULT_EVENT_ID
        defaultTransactionShouldBeFound("eventId.equals=" + DEFAULT_EVENT_ID);

        // Get all the transactionList where eventId equals to UPDATED_EVENT_ID
        defaultTransactionShouldNotBeFound("eventId.equals=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByEventIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where eventId not equals to DEFAULT_EVENT_ID
        defaultTransactionShouldNotBeFound("eventId.notEquals=" + DEFAULT_EVENT_ID);

        // Get all the transactionList where eventId not equals to UPDATED_EVENT_ID
        defaultTransactionShouldBeFound("eventId.notEquals=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByEventIdIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where eventId in DEFAULT_EVENT_ID or UPDATED_EVENT_ID
        defaultTransactionShouldBeFound("eventId.in=" + DEFAULT_EVENT_ID + "," + UPDATED_EVENT_ID);

        // Get all the transactionList where eventId equals to UPDATED_EVENT_ID
        defaultTransactionShouldNotBeFound("eventId.in=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByEventIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where eventId is not null
        defaultTransactionShouldBeFound("eventId.specified=true");

        // Get all the transactionList where eventId is null
        defaultTransactionShouldNotBeFound("eventId.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByEventIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where eventId is greater than or equal to DEFAULT_EVENT_ID
        defaultTransactionShouldBeFound("eventId.greaterThanOrEqual=" + DEFAULT_EVENT_ID);

        // Get all the transactionList where eventId is greater than or equal to UPDATED_EVENT_ID
        defaultTransactionShouldNotBeFound("eventId.greaterThanOrEqual=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByEventIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where eventId is less than or equal to DEFAULT_EVENT_ID
        defaultTransactionShouldBeFound("eventId.lessThanOrEqual=" + DEFAULT_EVENT_ID);

        // Get all the transactionList where eventId is less than or equal to SMALLER_EVENT_ID
        defaultTransactionShouldNotBeFound("eventId.lessThanOrEqual=" + SMALLER_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByEventIdIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where eventId is less than DEFAULT_EVENT_ID
        defaultTransactionShouldNotBeFound("eventId.lessThan=" + DEFAULT_EVENT_ID);

        // Get all the transactionList where eventId is less than UPDATED_EVENT_ID
        defaultTransactionShouldBeFound("eventId.lessThan=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByEventIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where eventId is greater than DEFAULT_EVENT_ID
        defaultTransactionShouldNotBeFound("eventId.greaterThan=" + DEFAULT_EVENT_ID);

        // Get all the transactionList where eventId is greater than SMALLER_EVENT_ID
        defaultTransactionShouldBeFound("eventId.greaterThan=" + SMALLER_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdBy not equals to DEFAULT_CREATED_BY
        defaultTransactionShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the transactionList where createdBy not equals to UPDATED_CREATED_BY
        defaultTransactionShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTransactionShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the transactionList where createdBy equals to UPDATED_CREATED_BY
        defaultTransactionShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdBy is not null
        defaultTransactionShouldBeFound("createdBy.specified=true");

        // Get all the transactionList where createdBy is null
        defaultTransactionShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdBy contains DEFAULT_CREATED_BY
        defaultTransactionShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the transactionList where createdBy contains UPDATED_CREATED_BY
        defaultTransactionShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTransactionShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the transactionList where createdBy does not contain UPDATED_CREATED_BY
        defaultTransactionShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTransactionsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultTransactionShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the transactionList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultTransactionShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTransactionsByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultTransactionShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the transactionList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultTransactionShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTransactionsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultTransactionShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the transactionList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultTransactionShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTransactionsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where lastModifiedBy is not null
        defaultTransactionShouldBeFound("lastModifiedBy.specified=true");

        // Get all the transactionList where lastModifiedBy is null
        defaultTransactionShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultTransactionShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the transactionList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultTransactionShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTransactionsByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultTransactionShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the transactionList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultTransactionShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionShouldBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TYPE.name())))
            .andExpect(jsonPath("$.[*].transactionStatus").value(hasItem(DEFAULT_TRANSACTION_STATUS.name())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].transactionAmount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].imageLink").value(hasItem(DEFAULT_IMAGE_LINK)))
            .andExpect(jsonPath("$.[*].closedBy").value(hasItem(DEFAULT_CLOSED_BY)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(Matchers.notNullValue()))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(Matchers.notNullValue()));

        // Check, that the count call also returns 1
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionShouldNotBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("0"));
    }

    @Test
    public void updateTransaction() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionStatus(DEFAULT_TRANSACTION_STATUS);
        Transaction savedTransaction = initTransactionDB();

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(savedTransaction.getId()).get();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction
            .transactionType(UPDATED_TYPE)
            .transactionAmount(UPDATED_AMOUNT)
            .description(UPDATED_DETAILS)
            .transactionStatus(TransactionStatus.CANCELLED);
        TransactionDTO transactionDTO = transactionMapper.toDto(updatedTransaction);

        restTransactionMockMvc.perform(put("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testTransaction.getTransactionType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTransaction.getTransactionAmount()).isEqualTo(DEFAULT_AMOUNT.setScale(2));
        assertThat(testTransaction.getDescription()).isEqualTo(UPDATED_DETAILS);
        assertThat(testTransaction.getTransactionStatus()).isEqualTo(TransactionStatus.CANCELLED);
    }

//    @Test
    public void updateTransaction_NonExistingRecord_ShouldThrow400() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionStatus(DEFAULT_TRANSACTION_STATUS);
        Transaction savedTransaction = initTransactionDB();

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Create the Transaction
        transaction.setId(Long.MAX_VALUE);
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        System.out.println("USER AUTH :: " + getLoggedInUser().getAuthorities());
        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc.perform(put("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

//    @Test
    public void updateTransaction_CancelledTransaction_ShouldThrow400() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionStatus(TransactionStatus.CANCELLED);
        Transaction savedTransaction = initTransactionDB();

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        Transaction updatedTransaction = transactionRepository.findById(savedTransaction.getId()).get();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction
            .transactionType(UPDATED_TYPE)
            .transactionAmount(UPDATED_AMOUNT)
            .description(UPDATED_DETAILS)
            .transactionStatus(TransactionStatus.SUCCESS);
        TransactionDTO transactionDTO = transactionMapper.toDto(updatedTransaction);

        restTransactionMockMvc.perform(put("/api/transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

//    @Test
    @WithCurrentCCAdministrator
    public void getTotalTransactionByEventId_WithCurrentCCAdministrator() throws Exception {
        this.event = createEventEntity();
        Event savedEvent = initEventDB();
        EventBudgetTotalDTO eventBudgetTotalDTO = new EventBudgetTotalDTO();
        for (int i = 0; i < 2; i ++) {
            Transaction transaction = createTransactionEntity();
            transaction.setEventId(savedEvent.getId());
            Transaction savedTransaction = transactionRepository.save(transaction);
            eventBudgetTotalDTO.addTotalIncome(savedTransaction.getTransactionAmount());
        }
        for (int i = 0; i < 2; i ++) {
            Transaction transaction = createTransactionEntity();
            transaction.setEventId(savedEvent.getId());
            transaction.setTransactionType(UPDATED_TYPE);
            Transaction savedTransaction = transactionRepository.save(transaction);
            eventBudgetTotalDTO.addTotalExpense(savedTransaction.getTransactionAmount());
        }

        restTransactionMockMvc.perform(get("/api/transactions/event/{eventId}/total", savedEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.totalExpense").value(eventBudgetTotalDTO.getTotalExpense().setScale(1)))
            .andExpect(jsonPath("$.totalIncome").value(eventBudgetTotalDTO.getTotalIncome().setScale(1)));
    }

//    @Test
    @WithEventCrew
    public void getTotalBudgetByEventId_WithEventCrew() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        EventBudgetTotalDTO eventBudgetTotalDTO = new EventBudgetTotalDTO();
        for (int i = 0; i < 2; i ++) {
            Transaction transaction = createTransactionEntity();
            transaction.setEventId(savedEventCrew.getEventId());
            Transaction savedTransaction = transactionRepository.save(transaction);
            eventBudgetTotalDTO.addTotalIncome(savedTransaction.getTransactionAmount());
        }
        for (int i = 0; i < 2; i ++) {
            Transaction transaction = createTransactionEntity();
            transaction.setEventId(savedEventCrew.getEventId());
            transaction.setTransactionType(UPDATED_TYPE);
            Transaction savedTransaction = transactionRepository.save(transaction);
            eventBudgetTotalDTO.addTotalExpense(savedTransaction.getTransactionAmount());
        }

        restTransactionMockMvc.perform(get("/api/transactions/event/{eventId}/total", savedEventCrew.getEventId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.totalExpense").value(eventBudgetTotalDTO.getTotalExpense().setScale(1)))
            .andExpect(jsonPath("$.totalIncome").value(eventBudgetTotalDTO.getTotalIncome().setScale(1)));
    }

    @Test
    @WithEventCrew
    public void getTotalBudgetByEventId_WithoutBudget() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        EventBudgetTotalDTO eventBudgetTotalDTO = new EventBudgetTotalDTO();

        restTransactionMockMvc.perform(get("/api/transactions/event/{eventId}/total", savedEventCrew.getEventId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.totalExpense").value(eventBudgetTotalDTO.getTotalExpense()))
            .andExpect(jsonPath("$.totalIncome").value(eventBudgetTotalDTO.getTotalIncome()));
    }

//    @Test
    @WithMockUser
    public void getTotalBudgetByEventId_WithNormalUser_ShouldThrow403() throws Exception {
        this.event = createEventEntity();
        Event savedEvent = initEventDB();
        EventBudgetTotalDTO eventBudgetTotalDTO = new EventBudgetTotalDTO();
        for (int i = 0; i < 2; i ++) {
            Transaction transaction = createTransactionEntity();
            transaction.setEventId(savedEvent.getId());
            Transaction savedTransaction = transactionRepository.save(transaction);
            eventBudgetTotalDTO.addTotalIncome(savedTransaction.getTransactionAmount());
        }
        for (int i = 0; i < 2; i ++) {
            Transaction transaction = createTransactionEntity();
            transaction.setEventId(savedEvent.getId());
            transaction.setTransactionType(UPDATED_TYPE);
            Transaction savedTransaction = transactionRepository.save(transaction);
            eventBudgetTotalDTO.addTotalExpense(savedTransaction.getTransactionAmount());
        }

        restTransactionMockMvc.perform(get("/api/transactions/event/{eventId}/total", savedEvent.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transaction.class);
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        Transaction transaction2 = new Transaction();
        transaction2.setId(transaction1.getId());
        assertThat(transaction1).isEqualTo(transaction2);
        transaction2.setId(2L);
        assertThat(transaction1).isNotEqualTo(transaction2);
        transaction1.setId(null);
        assertThat(transaction1).isNotEqualTo(transaction2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionDTO.class);
        TransactionDTO transactionDTO1 = new TransactionDTO();
        transactionDTO1.setId(1L);
        TransactionDTO transactionDTO2 = new TransactionDTO();
        assertThat(transactionDTO1).isNotEqualTo(transactionDTO2);
        transactionDTO2.setId(transactionDTO1.getId());
        assertThat(transactionDTO1).isEqualTo(transactionDTO2);
        transactionDTO2.setId(2L);
        assertThat(transactionDTO1).isNotEqualTo(transactionDTO2);
        transactionDTO1.setId(null);
        assertThat(transactionDTO1).isNotEqualTo(transactionDTO2);
    }

    @Test
    public void testEntityFromId() {
        assertThat(transactionMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(transactionMapper.fromId(null)).isNull();
    }

    private User getLoggedInUser() {
        return userService.getUserWithAuthorities()
            .orElseThrow(() -> new BadRequestException("User not login"));
    }

    private Transaction initTransactionDB() {
        return transactionRepository.saveAndFlush(transaction);
    }

    private Event initEventDB() {
        return eventRepository.saveAndFlush(event);
    }

    private EventCrew initEventCrewDB() { return eventCrewRepository.saveAndFlush(eventCrew); }

    private EventCrew getEventCrewByCurrentLoginUser() {
        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));
        List<EventCrew> eventCrewList = eventCrewRepository
            .findAllByUserId(currentUser.getId());
        assertThat(eventCrewList).hasSize(1);
        return eventCrewList.get(0);
    }
}
