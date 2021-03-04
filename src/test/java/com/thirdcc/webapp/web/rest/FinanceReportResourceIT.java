package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.domain.enumeration.TransactionType;
import com.thirdcc.webapp.repository.*;
import com.thirdcc.webapp.service.FinanceReportService;
import com.thirdcc.webapp.service.YearSessionService;
import com.thirdcc.webapp.utils.YearSessionUtils;
import com.thirdcc.webapp.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.thirdcc.webapp.web.rest.TestUtil.createFormattingConversionService;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = ClubmanagementApp.class)
class FinanceReportResourceIT {

    private static final BigDecimal DEFAULT_TRANSACTION_AMOUNT = new BigDecimal(11);
    private static final String DEFAULT_TRANSACTION_DETAILS = "DEFAULT_BUDGET_DETAILS";

    private static final BigDecimal DEFAULT_BUDGET_AMOUNT = new BigDecimal(22);
    private static final String DEFAULT_BUDGET_NAME = "DEFAULT_BUDGET_NAME";
    private static final String DEFAULT_BUDGET_DETAILS = "DEFAULT_BUDGET_DETAILS";

    private static final String DEFAULT_RECEIPT_URL = "DEFAULT_RECEIPT_URL";
    private static final String DEFAULT_FILE_NAME = "DEFAULT_FILE_NAME";
    private static final String DEFAULT_FILE_TYPE = "DEFAULT_FILE_TYPE";

    private static final String DEFAULT_EVENT_NAME = "DEFAULT_EVENT_NAME";
    private static final String DEFAULT_EVENT_DESCRIPTION = "DEFAULT_EVENT_DESCRIPTION";
    private static final String DEFAULT_EVENT_REMARKS = "DEFAULT_EVENT_REMARKS";
    private static final String DEFAULT_EVENT_VENUE = "DEFAULT_EVENT_VENUE";
    private static final Instant DEFAULT_EVENT_START_DATE = Instant.now().minus(5, ChronoUnit.DAYS);
    private static final Instant DEFAULT_EVENT_END_DATE = Instant.now().plus(5, ChronoUnit.DAYS);
    private static final BigDecimal DEFAULT_EVENT_FEE = new BigDecimal(2123);
    private static final Boolean DEFAULT_EVENT_REQUIRED_TRANSPORT = Boolean.TRUE;
    private static final EventStatus DEFAULT_EVENT_STATUS = EventStatus.OPEN;


    /** this Bean is mocked in
     * @see com.thirdcc.webapp.config.DateTimeProviderConfiguration
     */
    @Autowired
    public DateTimeProvider dateTimeProvider;

    @Autowired
    private FinanceReportService financeReportService;

    @Autowired
    private YearSessionService yearSessionService;

    @Autowired
    private YearSessionRepository yearSessionRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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

    private MockMvc restFinanceReportMockMvc;

    private Event event;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FinanceReportResource financeReportResource = new FinanceReportResource(financeReportService, yearSessionService);
        this.restFinanceReportMockMvc = MockMvcBuilders.standaloneSetup(financeReportResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    public static Event createEventEntity(EntityManager em) {
        Event event = new Event();
        event.setName(DEFAULT_EVENT_NAME);
        event.setDescription(DEFAULT_EVENT_DESCRIPTION);
        event.setRemarks(DEFAULT_EVENT_REMARKS);
        event.setVenue(DEFAULT_EVENT_VENUE);
        event.setStartDate(DEFAULT_EVENT_START_DATE);
        event.setEndDate(DEFAULT_EVENT_END_DATE);
        event.setFee(DEFAULT_EVENT_FEE);
        event.setRequiredTransport(DEFAULT_EVENT_REQUIRED_TRANSPORT);
        event.setStatus(DEFAULT_EVENT_STATUS);
        return event;
    }

    @BeforeEach
    public void initTest() {
        event = createEventEntity(em);
    }

    @AfterEach
    public void cleanUp() {
        eventRepository.deleteAll();
        receiptRepository.deleteAll();
        budgetRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    @Test
    public void getAllEventFinanceReport() throws Exception {
        Event savedEvent = initEventDB();
        Receipt savedReceipt = initReceiptDB();
        Budget incomeBudget = initBudgetDB(savedEvent, TransactionType.INCOME);
        Budget expenseBudget = initBudgetDB(savedEvent, TransactionType.EXPENSE);
        Transaction incomeTransaction = initTransactionDB(savedEvent, savedReceipt, TransactionType.INCOME);
        Transaction expenseTransaction = initTransactionDB(savedEvent, savedReceipt, TransactionType.EXPENSE);

        restFinanceReportMockMvc.perform(get("/api/finance-report?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].eventDTO.id").value(hasItem(savedEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].totalBudgetIncome").value(hasItem(DEFAULT_BUDGET_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalBudgetExpenses").value(hasItem(DEFAULT_BUDGET_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalIncome").value(hasItem(DEFAULT_TRANSACTION_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalExpenses").value(hasItem(DEFAULT_TRANSACTION_AMOUNT.doubleValue())));
    }

    @Test
    public void getFinanceReportByEventId() throws Exception {
        Event savedEvent = initEventDB();
        Receipt savedReceipt = initReceiptDB();
        Budget incomeBudget = initBudgetDB(savedEvent, TransactionType.INCOME);
        Budget expenseBudget = initBudgetDB(savedEvent, TransactionType.EXPENSE);
        Transaction incomeTransaction = initTransactionDB(savedEvent, savedReceipt, TransactionType.INCOME);
        Transaction expenseTransaction = initTransactionDB(savedEvent, savedReceipt, TransactionType.EXPENSE);

        restFinanceReportMockMvc.perform(get("/api/finance-report/event/{eventId}", savedEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.eventDTO.id").value(savedEvent.getId().intValue()))
            .andExpect(jsonPath("$.totalBudgetIncome").value(DEFAULT_BUDGET_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.totalBudgetExpenses").value(DEFAULT_BUDGET_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.totalIncome").value(DEFAULT_TRANSACTION_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.totalExpenses").value(DEFAULT_TRANSACTION_AMOUNT.doubleValue()));
    }

    @Test
    public void getFinanceReportByEventId_WithNonExistingEventId() throws Exception {
        restFinanceReportMockMvc.perform(get("/api/finance-report/event/{eventId}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getFinanceReportByYearSession() throws Exception {
        //mock createdDate
        LocalDateTime transactionLocalDateTime =  LocalDateTime.of(1998, 1, 20, 0, 0, 0);
        Mockito
            .when(dateTimeProvider.getNow())
            .thenReturn(Optional.of(transactionLocalDateTime));

        Event savedEvent = initEventDB();
        Receipt savedReceipt = initReceiptDB();
        Instant transactionDate = transactionLocalDateTime.atZone(ZoneId.systemDefault()).toInstant();
        YearSession savedYearSession = initYearSessionDB(transactionDate);
        Transaction incomeTransaction = initTransactionDB(savedEvent, savedReceipt, TransactionType.INCOME);
        Transaction expenseTransaction = initTransactionDB(savedEvent, savedReceipt, TransactionType.EXPENSE);

        restFinanceReportMockMvc.perform(
            get("/api/finance-report/year-session")
                .param("yearSessionId", savedYearSession.getId().toString())
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.INCOME.JANUARY").value(DEFAULT_TRANSACTION_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.INCOME.FEBRUARY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.MARCH").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.APRIL").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.MAY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.JUNE").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.JULY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.AUGUST").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.SEPTEMBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.OCTOBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.NOVEMBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.DECEMBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.JANUARY").value(DEFAULT_TRANSACTION_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.FEBRUARY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.MARCH").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.APRIL").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.MAY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.JUNE").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.JULY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.AUGUST").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.SEPTEMBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.OCTOBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.NOVEMBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.DECEMBER").value(BigDecimal.ZERO.doubleValue()));
    }

    @Test
    public void getFinanceReportByYearSession_WithNoTransaction() throws Exception {
        YearSession savedYearSession = initYearSessionDB(Instant.now());

        restFinanceReportMockMvc.perform(get("/api/finance-report/year-session"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.INCOME.JANUARY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.FEBRUARY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.MARCH").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.APRIL").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.MAY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.JUNE").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.JULY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.AUGUST").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.SEPTEMBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.OCTOBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.NOVEMBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.INCOME.DECEMBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.JANUARY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.FEBRUARY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.MARCH").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.APRIL").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.MAY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.JUNE").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.JULY").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.AUGUST").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.SEPTEMBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.OCTOBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.NOVEMBER").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.EXPENSE.DECEMBER").value(BigDecimal.ZERO.doubleValue()));
    }


    @Test
    public void getFinanceReportByYearSession_WithInvalidYearSessionId() throws Exception {
        YearSession savedYearSession = initYearSessionDB(Instant.now());

        restFinanceReportMockMvc.perform(
            get("/api/finance-report/year-session")
                .param("yearSessionId", String.valueOf(Long.MAX_VALUE))
        )
            .andExpect(status().isBadRequest());
    }

    private Event initEventDB() {
        return eventRepository.saveAndFlush(event);
    }

    private Receipt initReceiptDB() {
        Receipt receipt = new Receipt()
            .receiptUrl(DEFAULT_RECEIPT_URL)
            .fileName(DEFAULT_FILE_NAME)
            .fileType(DEFAULT_FILE_TYPE);
        return receiptRepository.saveAndFlush(receipt);
    }

    private Budget initBudgetDB(Event savedEvent, TransactionType transactionType) {
        Budget budget = new Budget();
        budget.setEventId(savedEvent.getId());
        budget.setAmount(DEFAULT_BUDGET_AMOUNT);
        budget.setType(transactionType);
        budget.setName(DEFAULT_BUDGET_NAME);
        budget.setDetails(DEFAULT_BUDGET_DETAILS);
        return budgetRepository.saveAndFlush(budget);
    }

    private Transaction initTransactionDB(Event savedEvent, Receipt savedReceipt, TransactionType transactionType) {
        Transaction transaction = new Transaction();
        transaction.setEventId(savedEvent.getId());
        transaction.setReceiptId(savedReceipt.getId());
        transaction.setAmount(DEFAULT_TRANSACTION_AMOUNT);
        transaction.setType(transactionType);
        transaction.setDetails(DEFAULT_TRANSACTION_DETAILS);
        return transactionRepository.saveAndFlush(transaction);
    }

    private YearSession initYearSessionDB(Instant instant) {
        YearSession yearSession = new YearSession();
        String value = YearSessionUtils.toYearSession(instant);
        yearSession.setValue(value);
        return yearSessionRepository.saveAndFlush(yearSession);
    }
}
