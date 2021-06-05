package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.WithCurrentCCAdministrator;
import com.thirdcc.webapp.annotations.authorization.WithEventHead;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.domain.enumeration.TransactionStatus;
import com.thirdcc.webapp.domain.enumeration.TransactionType;
import com.thirdcc.webapp.repository.*;
import com.thirdcc.webapp.service.FinanceReportService;
import com.thirdcc.webapp.service.YearSessionService;
import com.thirdcc.webapp.utils.YearSessionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@InitYearSession
@WithCurrentCCAdministrator
class FinanceReportResourceIT {

    private static final BigDecimal DEFAULT_TRANSACTION_AMOUNT = new BigDecimal(11);
    private static final String DEFAULT_TRANSACTION_DETAILS = "DEFAULT_BUDGET_DETAILS";
    private static final TransactionStatus DEFAULT_TRANSACTION_STATUS = TransactionStatus.PENDING;
    private static final String DEFAULT_INCOME_TRANSACTION_TITLE = "DEFAULT_INCOME_TRANSACTION_TITLE";
    private static final String DEFAULT_EXPENSE_TRANSACTION_TITLE = "DEFAULT_EXPENSE_TRANSACTION_TITLE";
    private static final Instant DEFAULT_TRANSACTION_DATE = Instant.now();

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
    private EntityManager em;

    @Autowired
    private MockMvc restFinanceReportMockMvc;

    private Event event;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
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
        Transaction incomeTransaction = initTransactionDB(DEFAULT_INCOME_TRANSACTION_TITLE, savedEvent, savedReceipt, TransactionType.INCOME, DEFAULT_TRANSACTION_DATE);
        Transaction expenseTransaction = initTransactionDB(DEFAULT_EXPENSE_TRANSACTION_TITLE, savedEvent, savedReceipt, TransactionType.EXPENSE, DEFAULT_TRANSACTION_DATE);

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
    @WithEventHead
    public void getAllEventFinanceReport_IsNotAdmin_ShouldReturnIsForbidden403() throws Exception {
        Event savedEvent = initEventDB();
        restFinanceReportMockMvc.perform(get("/api/finance-report?sort=id,desc"))
            .andExpect(status().isForbidden());
    }
    
    @Test
    public void getFinanceReportByEventId() throws Exception {
        Event savedEvent = initEventDB();
        Receipt savedReceipt = initReceiptDB();
        Budget incomeBudget = initBudgetDB(savedEvent, TransactionType.INCOME);
        Budget expenseBudget = initBudgetDB(savedEvent, TransactionType.EXPENSE);
        Transaction incomeTransaction = initTransactionDB(DEFAULT_INCOME_TRANSACTION_TITLE, savedEvent, savedReceipt, TransactionType.INCOME, DEFAULT_TRANSACTION_DATE);
        Transaction expenseTransaction = initTransactionDB(DEFAULT_EXPENSE_TRANSACTION_TITLE, savedEvent, savedReceipt, TransactionType.EXPENSE, DEFAULT_TRANSACTION_DATE);

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
    @WithEventHead
    public void getFinanceReportByEventId_IsNotAdmin_ShouldReturnIsForbidden403() throws Exception {
        Event savedEvent = initEventDB();
        restFinanceReportMockMvc.perform(get("/api/finance-report/event/{eventId}", savedEvent.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getFinanceReportByYearSession() throws Exception {
        //mock createdDate
        LocalDateTime transactionLocalDateTime =  LocalDateTime.of(LocalDateTime.now().getYear(), 1, 20, 0, 0, 0);
        Mockito
            .when(dateTimeProvider.getNow())
            .thenReturn(Optional.of(transactionLocalDateTime));
        Event savedEvent = initEventDB();
        Receipt savedReceipt = initReceiptDB();
        Long currentYearSessionId = yearSessionService.getCurrentYearSession().getId();
        Instant transactionDate = transactionLocalDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Transaction incomeTransaction = initTransactionDB(DEFAULT_INCOME_TRANSACTION_TITLE, savedEvent, savedReceipt, TransactionType.INCOME, transactionDate);
        Transaction expenseTransaction = initTransactionDB(DEFAULT_EXPENSE_TRANSACTION_TITLE, savedEvent, savedReceipt, TransactionType.EXPENSE, transactionDate);

        restFinanceReportMockMvc.perform(
            get("/api/finance-report/year-session")
                .param("yearSessionId", currentYearSessionId.toString())
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
        restFinanceReportMockMvc.perform(
            get("/api/finance-report/year-session")
                .param("yearSessionId", String.valueOf(Long.MAX_VALUE))
        )
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithEventHead
    public void getFinanceReportByYearSession_IsNotAdmin_ShouldReturnIsForbidden403() throws Exception {
        restFinanceReportMockMvc.perform(get("/api/finance-report/year-session"))
            .andExpect(status().isForbidden());
    }
    
    @Test
    public void getFinanceReportStatisticOfCurrentYearSession() throws Exception {
        Event savedEvent = initEventDB();
        Receipt savedReceipt = initReceiptDB();
        Instant transactionDate = DEFAULT_TRANSACTION_DATE;
        Transaction incomeTransaction = initTransactionDB(DEFAULT_INCOME_TRANSACTION_TITLE, savedEvent, savedReceipt, TransactionType.INCOME, transactionDate);
        Transaction expenseTransaction = initTransactionDB(DEFAULT_EXPENSE_TRANSACTION_TITLE, savedEvent, savedReceipt, TransactionType.EXPENSE, transactionDate);

        restFinanceReportMockMvc.perform(get("/api/finance-report/current-year-session-statistic"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.realisedIncome").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.pendingIncome").value(DEFAULT_TRANSACTION_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.realisedExpenses").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.pendingExpenses").value(DEFAULT_TRANSACTION_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.invalidExpenses").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.badDebt").value(BigDecimal.ZERO.doubleValue()));
    }

    @Test
    public void getFinanceReportOfCurrentYearSession_WithNoTransaction() throws Exception {
        restFinanceReportMockMvc.perform(get("/api/finance-report/current-year-session-statistic"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.realisedIncome").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.pendingIncome").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.realisedExpenses").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.pendingExpenses").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.invalidExpenses").value(BigDecimal.ZERO.doubleValue()))
            .andExpect(jsonPath("$.badDebt").value(BigDecimal.ZERO.doubleValue()));
    }
    
    @Test
    @WithEventHead
    public void getFinanceReportOfCurrentYearSession_IsNotAdmin_ShouldReturnIsForbidden403() throws Exception {
        restFinanceReportMockMvc.perform(get("/api/finance-report/current-year-session-statistic"))
            .andExpect(status().isForbidden());
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

    private Transaction initTransactionDB(String title, Event savedEvent, Receipt savedReceipt, TransactionType transactionType, Instant transactionDate) {
        Transaction transaction = new Transaction();
        transaction.setTitle(DEFAULT_FILE_TYPE);
        transaction.setEventId(savedEvent.getId());
        transaction.setTransactionAmount(DEFAULT_TRANSACTION_AMOUNT);
        transaction.setTransactionType(transactionType);
        transaction.setDescription(DEFAULT_TRANSACTION_DETAILS);
        transaction.setTransactionStatus(DEFAULT_TRANSACTION_STATUS);
        transaction.setTransactionDate(transactionDate);
        return transactionRepository.saveAndFlush(transaction);
    }
}
