package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.WithCurrentCCAdministrator;
import com.thirdcc.webapp.annotations.authorization.WithEventCrew;
import com.thirdcc.webapp.annotations.authorization.WithNormalUser;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.domain.enumeration.*;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.*;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.ImageStorageService;
import com.thirdcc.webapp.service.UserService;
import com.thirdcc.webapp.service.dto.ImageStorageDTO;
import com.thirdcc.webapp.service.dto.TransactionDTO;
import com.thirdcc.webapp.service.mapper.TransactionMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link TransactionResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = "ADMIN")
@InitYearSession
public class TransactionResourceIT {

    private static final String TRANSACTION_DTO_REQUEST_PARAM = "transactionDTO";
    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final Long DEFAULT_RECEIPT_ID = 1L;
    private static final Long UPDATED_RECEIPT_ID = 2L;

    private static final TransactionType DEFAULT_TRANSACTION_TYPE = TransactionType.INCOME;
    private static final TransactionType UPDATED_TRANSACTION_TYPE = TransactionType.EXPENSE;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_TITLE = "TRANSACTION TITLE";
    private static final String DEFAULT_DESCRIPTION = "TRANSACTION_DETAILS";
    private static final String UPDATED_DESCRIPTION = "TRANSACTION_UPDATED_DETAILS";

    private static final TransactionStatus DEFAULT_TRANSACTION_STATUS = TransactionStatus.PENDING;
    private static final TransactionStatus UPDATED_TRANSACTION_STATUS = TransactionStatus.COMPLETED;

    // Event Default data
    private static final String DEFAULT_EVENT_NAME = "DEFAULT_EVENT_NAME";
    private static final String DEFAULT_EVENT_DESCRIPTION = "DEFAULT_EVENT_DESCRIPTION";
    private static final String DEFAULT_EVENT_REMARKS = "DEFAULT_EVENT_REMARKS";
    private static final String DEFAULT_EVENT_VENUE = "DEFAULT_EVENT_VENUE";
    private static final Instant DEFAULT_EVENT_START_DATE = Instant.now().minus(5, ChronoUnit.DAYS);
    private static final Instant DEFAULT_EVENT_END_DATE = Instant.now().plus(5, ChronoUnit.DAYS);
    private static final Instant DEFAULT_TRANSACTION_DATE = Instant.now();
    private static final BigDecimal DEFAULT_EVENT_FEE = new BigDecimal(2123);
    private static final EventStatus DEFAULT_EVENT_STATUS = EventStatus.OPEN;
    private static final String DEFAULT_RECEIPT_IMAGE_TYPE = "DEFAULT_RECEIPT_IMAGE_TYPE";
    private static final String DEFAULT_RECEIPT_IMAGE_FILENAME = "DEFAULT_RECEIPT_IMAGE_FILENAME";
    private static final String DEFAULT_RECEIPT_IMAGE_CONTENT = "DEAFULT_RECEIPT_IMAGE_CONTENT";
    private static final MockMultipartFile MOCK_MULTIPART_FILE =  new MockMultipartFile("multipartFile", "Mocked Content".getBytes());
    public static final String DEFAULT_IMAGE_LINK = "https://gcp/abc.jpg";

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
    private ImageStorageService imageStorageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc restTransactionMockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private EventCrewRepository eventCrewRepository;

    private EventCrew eventCrew;

    private Transaction transaction;

    private Event event;

    private final MockMultipartHttpServletRequestBuilder putMultipartRequestBuilder = (MockMultipartHttpServletRequestBuilder) multipart("/api/transactions").with(request -> {
        request.setMethod("PUT");
        return request;
    });

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
            .eventId(null)
            .transactionType(DEFAULT_TRANSACTION_TYPE)
            .transactionAmount(DEFAULT_AMOUNT)
            .description(DEFAULT_DESCRIPTION)
            .transactionStatus(DEFAULT_TRANSACTION_STATUS)
            .transactionDate(DEFAULT_TRANSACTION_DATE);
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
        eventCrew = createEventCrew();
    }

    @BeforeEach
    public void initStub() throws IOException {
        ImageStorageDTO mockImageSavingResult = new ImageStorageDTO();
        mockImageSavingResult.setId(1L);
        mockImageSavingResult.setImageUrl(DEFAULT_IMAGE_LINK);
        mockImageSavingResult.setFileType("jpeg");
        mockImageSavingResult.setFileName("Receipt Image");

        when(imageStorageService.save(new ImageStorageDTO(), MOCK_MULTIPART_FILE)).thenReturn(mockImageSavingResult);
    }

    @AfterEach
    public void cleanUp() {
        transactionRepository.deleteAll();
        eventRepository.deleteAll();
        receiptRepository.deleteAll();
        eventCrewRepository.deleteAll();
    }

    @Test
    public void createNullEventPendingIncome_UserIsAdmin_ShouldSuccess() throws Exception {
        // Initialize the database
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction
        restTransactionMockMvc.perform(multipart("/api/transactions")
            .file(MOCK_MULTIPART_FILE)
            .param("title", DEFAULT_TITLE)
            .param("eventId", "")
            .param("transactionType", DEFAULT_TRANSACTION_TYPE.name())
            .param("transactionAmount", DEFAULT_AMOUNT.toString())
            .param("description", DEFAULT_DESCRIPTION)
            .param("transactionStatus", DEFAULT_TRANSACTION_STATUS.name())
            .param("transactionDate", DEFAULT_TRANSACTION_DATE.toString())
        ).andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);

        // Validate the saved Transaction
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTransaction.getEventId()).isEqualTo(null);
        assertThat(testTransaction.getTransactionType()).isEqualTo(DEFAULT_TRANSACTION_TYPE);
        assertThat(testTransaction.getTransactionAmount()).isEqualTo(DEFAULT_AMOUNT.setScale(2, RoundingMode.HALF_UP));
        assertThat(testTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTransaction.getTransactionStatus()).isEqualTo(DEFAULT_TRANSACTION_STATUS);
        assertThat(testTransaction.getTransactionDate()).isEqualTo(DEFAULT_TRANSACTION_DATE);
        assertThat(testTransaction.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
    }

    @Test
    @WithNormalUser
    public void createNullEventTransactionPendingIncome_UserIsUser_ShouldThrow403() throws Exception {
        // Initialize the database
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction
        restTransactionMockMvc.perform(multipart("/api/transactions")
            .file(MOCK_MULTIPART_FILE)
            .param("title", DEFAULT_TITLE)
            .param("eventId", "")
            .param("transactionType", DEFAULT_TRANSACTION_TYPE.name())
            .param("transactionAmount", DEFAULT_AMOUNT.toString())
            .param("description", DEFAULT_DESCRIPTION)
            .param("transactionStatus", DEFAULT_TRANSACTION_STATUS.name())
            .param("transactionDate", DEFAULT_TRANSACTION_DATE.toString())
        ).andExpect(status().isForbidden());

        // Validate the Transaction not in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithEventCrew
    public void createNullEventTransactionPendingIncome_UserIsEventCrew_ShouldThrow403() throws Exception {
        // Initialize the database
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction
        restTransactionMockMvc.perform(multipart("/api/transactions")
            .file(MOCK_MULTIPART_FILE)
            .param("title", DEFAULT_TITLE)
            .param("eventId", "")
            .param("transactionType", DEFAULT_TRANSACTION_TYPE.name())
            .param("transactionAmount", DEFAULT_AMOUNT.toString())
            .param("description", DEFAULT_DESCRIPTION)
            .param("transactionStatus", DEFAULT_TRANSACTION_STATUS.name())
            .param("transactionDate", DEFAULT_TRANSACTION_DATE.toString())
        ).andExpect(status().isForbidden());

        // Validate the Transaction not in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createNullEventTransactionPendingIncome_UserIsCCAdmin_ShouldSuccess() throws Exception {
        // Initialize the database
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction
        restTransactionMockMvc.perform(multipart("/api/transactions")
            .file(MOCK_MULTIPART_FILE)
            .param("title", DEFAULT_TITLE)
            .param("eventId", "")
            .param("transactionType", DEFAULT_TRANSACTION_TYPE.name())
            .param("transactionAmount", DEFAULT_AMOUNT.toString())
            .param("description", DEFAULT_DESCRIPTION)
            .param("transactionStatus", DEFAULT_TRANSACTION_STATUS.name())
            .param("transactionDate", DEFAULT_TRANSACTION_DATE.toString())
        ).andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);

        // Validate the saved Transaction
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTransaction.getEventId()).isEqualTo(null);
        assertThat(testTransaction.getTransactionType()).isEqualTo(DEFAULT_TRANSACTION_TYPE);
        assertThat(testTransaction.getTransactionAmount()).isEqualTo(DEFAULT_AMOUNT.setScale(2, RoundingMode.HALF_UP));
        assertThat(testTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTransaction.getTransactionStatus()).isEqualTo(DEFAULT_TRANSACTION_STATUS);
        assertThat(testTransaction.getTransactionDate()).isEqualTo(DEFAULT_TRANSACTION_DATE);
        assertThat(testTransaction.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
    }

    @Test
    public void createEventTransactionPendingIncome_UserIsAdmin_ShouldSuccess() throws Exception {
        // Initialise event
        Event savedEvent = initEventDB();
        // Initialize the database
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction
        restTransactionMockMvc.perform(multipart("/api/transactions")
            .file(MOCK_MULTIPART_FILE)
            .param("title", DEFAULT_TITLE)
            .param("eventId", savedEvent.getId().toString())
            .param("transactionType", DEFAULT_TRANSACTION_TYPE.name())
            .param("transactionAmount", DEFAULT_AMOUNT.toString())
            .param("description", DEFAULT_DESCRIPTION)
            .param("transactionStatus", DEFAULT_TRANSACTION_STATUS.name())
            .param("transactionDate", DEFAULT_TRANSACTION_DATE.toString())
        ).andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);

        // Validate the saved Transaction
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTransaction.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testTransaction.getTransactionType()).isEqualTo(DEFAULT_TRANSACTION_TYPE);
        assertThat(testTransaction.getTransactionAmount()).isEqualTo(DEFAULT_AMOUNT.setScale(2, RoundingMode.HALF_UP));
        assertThat(testTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTransaction.getTransactionStatus()).isEqualTo(DEFAULT_TRANSACTION_STATUS);
        assertThat(testTransaction.getTransactionDate()).isEqualTo(DEFAULT_TRANSACTION_DATE);
        assertThat(testTransaction.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
    }

    @Test
    @WithNormalUser
    public void createEventTransactionPendingIncome_UserIsNormalUser_ShouldThrow403() throws Exception {
        // Initialise event
        Event savedEvent = initEventDB();
        // Initialize the database
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction
        restTransactionMockMvc.perform(multipart("/api/transactions")
            .file(MOCK_MULTIPART_FILE)
            .param("title", DEFAULT_TITLE)
            .param("eventId", savedEvent.getId().toString())
            .param("transactionType", DEFAULT_TRANSACTION_TYPE.name())
            .param("transactionAmount", DEFAULT_AMOUNT.toString())
            .param("description", DEFAULT_DESCRIPTION)
            .param("transactionStatus", DEFAULT_TRANSACTION_STATUS.name())
            .param("transactionDate", DEFAULT_TRANSACTION_DATE.toString())
        ).andExpect(status().isForbidden());

        // Validate the Transaction not in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithEventCrew
    public void createEventTransactionPendingIncome_UserIsEventCrew_ShouldThrow403() throws Exception {
        // Initialise event
        Event savedEvent = initEventDB();
        // Initialize the database
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction
        restTransactionMockMvc.perform(multipart("/api/transactions")
            .file(MOCK_MULTIPART_FILE)
            .param("title", DEFAULT_TITLE)
            .param("eventId", savedEvent.getId().toString())
            .param("transactionType", DEFAULT_TRANSACTION_TYPE.name())
            .param("transactionAmount", DEFAULT_AMOUNT.toString())
            .param("description", DEFAULT_DESCRIPTION)
            .param("transactionStatus", DEFAULT_TRANSACTION_STATUS.name())
            .param("transactionDate", DEFAULT_TRANSACTION_DATE.toString())
        ).andExpect(status().isForbidden());

        // Validate the Transaction not in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void updateNullEventTransactionPendingIncome_ToCompleted_UserIsAdmin_ShouldSuccess() throws Exception {
        // Create Transaction
        transactionRepository.saveAndFlush(transaction);
        // Get Database size after creating transaction
        int databaseSizeAfterCreate = transactionRepository.findAll().size();
        // find created transaction
        Transaction createdTransaction = transactionRepository.findById(transaction.getId()).get();

        // Update the transaction
        restTransactionMockMvc.perform(putMultipartRequestBuilder
            .file(MOCK_MULTIPART_FILE)
            .param("id", createdTransaction.getId().toString())
            .param("title", createdTransaction.getTitle())
            .param("eventId", "")
            .param("transactionType", createdTransaction.getTransactionType().name())
            .param("transactionAmount", createdTransaction.getTransactionAmount().toString())
            .param("description", createdTransaction.getDescription())
            .param("transactionStatus", UPDATED_TRANSACTION_STATUS.name())
            .param("transactionDate", createdTransaction.getTransactionDate().toString())
        ).andExpect(status().isOk());

        List<Transaction> transactionList = transactionRepository.findAll();
        Transaction updatedTransaction = transactionList.get(transactionList.size() - 1);

        // Validate the Transaction count stays the same after creating 1 transaction
        assertThat(transactionList).hasSize(databaseSizeAfterCreate);
        assertThat(updatedTransaction.getTransactionStatus()).isEqualTo(UPDATED_TRANSACTION_STATUS);
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
