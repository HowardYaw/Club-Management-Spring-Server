package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.WithCurrentCCAdministrator;
import com.thirdcc.webapp.annotations.authorization.WithEventCrew;
import com.thirdcc.webapp.annotations.authorization.WithEventHead;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.domain.Budget;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.*;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.BudgetService;
import com.thirdcc.webapp.service.dto.BudgetDTO;
import com.thirdcc.webapp.service.dto.EventBudgetTotalDTO;
import com.thirdcc.webapp.service.mapper.BudgetMapper;

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

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.thirdcc.webapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thirdcc.webapp.domain.enumeration.TransactionType;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@Link BudgetResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@InitYearSession
public class BudgetResourceIT {

    private static final String ENTITY_API_URL = "/api/event-budget";

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long SMALLER_EVENT_ID = DEFAULT_EVENT_ID - 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(11, 2);
    private static final BigDecimal SMALLER_AMOUNT = BigDecimal.valueOf(11, 2).subtract(BigDecimal.ONE);
    private static final BigDecimal UPDATED_AMOUNT = BigDecimal.valueOf(21, 2);

    private static final TransactionType DEFAULT_TYPE = TransactionType.INCOME;
    private static final TransactionType UPDATED_TYPE = TransactionType.EXPENSE;

    private static final String DEFAULT_NAME = "DEFAULT_NAME";
    private static final String UPDATED_NAME = "UPDATED_NAME";

    private static final String DEFAULT_DETAILS = "DEFAULT_DETAILS";
    private static final String UPDATED_DETAILS = "UPDATED_DETAILS";

    private static final String DEFAULT_EVENT_NAME = "DEFAULT_EVENT_NAME";
    private static final String DEFAULT_EVENT_DESCRIPTION = "DEFAULT_EVENT_DESCRIPTION";
    private static final String DEFAULT_EVENT_REMARKS = "DEFAULT_EVENT_REMARKS";
    private static final String DEFAULT_EVENT_VENUE = "DEFAULT_EVENT_VENUE";
    private static final Instant DEFAULT_EVENT_START_DATE = Instant.now().minus(5, ChronoUnit.DAYS);
    private static final Instant DEFAULT_EVENT_END_DATE = Instant.now().plus(5, ChronoUnit.DAYS);
    private static final BigDecimal DEFAULT_EVENT_FEE = new BigDecimal(2123);
    private static final Boolean DEFAULT_EVENT_REQUIRED_TRANSPORT = Boolean.TRUE;
    private static final EventStatus DEFAULT_EVENT_STATUS = EventStatus.OPEN;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventCrewRepository eventCrewRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private BudgetMapper budgetMapper;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBudgetMockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void cleanUp() {
        eventRepository.deleteAll();
        budgetRepository.deleteAll();
    }

    public static Event createEventEntity() {
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

    public static Budget createBudgetEntity() {
        Budget budget = new Budget()
            .eventId(DEFAULT_EVENT_ID)
            .amount(DEFAULT_AMOUNT)
            .type(DEFAULT_TYPE)
            .name(DEFAULT_NAME)
            .details(DEFAULT_DETAILS);
        return budget;
    }

    public static Budget createUpdateBudgetEntity() {
        Budget budget = new Budget()
            .eventId(UPDATED_EVENT_ID)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .name(UPDATED_NAME)
            .details(UPDATED_DETAILS);
        return budget;
    }

    @Test
    @WithCurrentCCAdministrator
    public void createBudget_WithCurrentCCAdministrator() throws Exception {
        int databaseSizeBeforeCreate = budgetRepository.findAll().size();

        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        BudgetDTO budgetDTO = budgetMapper.toDto(budget);

        restBudgetMockMvc.perform(post("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isCreated());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeCreate + 1);
        Budget testBudget = budgetList.get(budgetList.size() - 1);
        assertThat(testBudget.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testBudget.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testBudget.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBudget.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBudget.getDetails()).isEqualTo(DEFAULT_DETAILS);
    }

    @Test
    @WithEventHead
    public void createBudget_WithEventHead() throws Exception {
        int databaseSizeBeforeCreate = budgetRepository.findAll().size();

        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEventCrew.getEventId());
        BudgetDTO budgetDTO = budgetMapper.toDto(budget);

        restBudgetMockMvc.perform(post("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isCreated());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeCreate + 1);
        Budget testBudget = budgetList.get(budgetList.size() - 1);
        assertThat(testBudget.getEventId()).isEqualTo(savedEventCrew.getEventId());
        assertThat(testBudget.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testBudget.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBudget.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBudget.getDetails()).isEqualTo(DEFAULT_DETAILS);
    }

    @Test
    @WithEventCrew
    public void createBudget_WithEventCrew_ShouldThrow403() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEventCrew.getEventId());
        BudgetDTO budgetDTO = budgetMapper.toDto(budget);

        restBudgetMockMvc.perform(post("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isForbidden());

    }

    @Test
    @WithCurrentCCAdministrator
    public void createBudget_WithIdInDTO_ShouldThrow400() throws Exception {
        int databaseSizeBeforeCreate = budgetRepository.findAll().size();

        Budget budget = createBudgetEntity();
        budget.setId(1L);
        BudgetDTO budgetDTO = budgetMapper.toDto(budget);

        restBudgetMockMvc.perform(post("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isBadRequest());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createBudget_WithEventCancelled_ShouldThrow400() throws Exception {
        int databaseSizeBeforeCreate = budgetRepository.findAll().size();

        Event event = createEventEntity();
        event.setStatus(EventStatus.CANCELLED);
        Event savedEvent = initEventDB(event);
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        BudgetDTO budgetDTO = budgetMapper.toDto(budget);

        assertThat(savedEvent.getStatus()).isEqualByComparingTo(EventStatus.CANCELLED);
        assertThat(budgetDTO.getEventId()).isEqualTo(savedEvent.getId());

        restBudgetMockMvc.perform(post("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isBadRequest());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createBudget_WithEventEnded_ShouldThrow400() throws Exception {
        int databaseSizeBeforeCreate = budgetRepository.findAll().size();

        Event event = createEventEntity();
        event.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS));
        Event savedEvent = initEventDB(event);
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        BudgetDTO budgetDTO = budgetMapper.toDto(budget);

        assertThat(savedEvent.getEndDate()).isBefore(Instant.now());
        assertThat(budgetDTO.getEventId()).isEqualTo(savedEvent.getId());

        restBudgetMockMvc.perform(post("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isBadRequest());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void getAllBudgetsByEventId_WithCurrentCCAdministrator() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        Budget savedBudget = initBudgetDB(budget);

        restBudgetMockMvc.perform(
            get("/api/event-budget/event/{eventId}?sort=id,desc", savedEvent.getId())
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedBudget.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(savedEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));
    }

    @Test
    @WithEventCrew
    public void getAllBudgetsByEventId_WithEventCrew() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEventCrew.getEventId());
        Budget savedBudget = initBudgetDB(budget);

        restBudgetMockMvc.perform(
            get("/api/event-budget/event/{eventId}?sort=id,desc", savedEventCrew.getEventId())
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedBudget.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(savedEventCrew.getEventId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));
    }

    @Test
    @WithMockUser
    public void getAllBudgetsByEventId_WithMockUser_ShouldThrow403() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        Budget savedBudget = initBudgetDB(budget);

        restBudgetMockMvc.perform(
            get("/api/event-budget/event/{eventId}?sort=id,desc", savedEvent.getId())
        )
            .andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    void getBudgetsByIdFiltering() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        Long id = budget.getId();

        defaultBudgetShouldBeFound(savedBudget, "id.equals=" + id);
        defaultBudgetShouldNotBeFound("id.notEquals=" + id);

        defaultBudgetShouldBeFound(savedBudget, "id.greaterThanOrEqual=" + id);
        defaultBudgetShouldNotBeFound("id.greaterThan=" + id);

        defaultBudgetShouldBeFound(savedBudget, "id.lessThanOrEqual=" + id);
        defaultBudgetShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBudgetsByEventIdIsEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where eventId equals to DEFAULT_EVENT_ID
        defaultBudgetShouldBeFound(savedBudget, "eventId.equals=" + DEFAULT_EVENT_ID);

        // Get all the budgetList where eventId equals to UPDATED_EVENT_ID
        defaultBudgetShouldNotBeFound("eventId.equals=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllBudgetsByEventIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where eventId not equals to DEFAULT_EVENT_ID
        defaultBudgetShouldNotBeFound("eventId.notEquals=" + DEFAULT_EVENT_ID);

        // Get all the budgetList where eventId not equals to UPDATED_EVENT_ID
        defaultBudgetShouldBeFound(savedBudget, "eventId.notEquals=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllBudgetsByEventIdIsInShouldWork() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where eventId in DEFAULT_EVENT_ID or UPDATED_EVENT_ID
        defaultBudgetShouldBeFound(savedBudget, "eventId.in=" + DEFAULT_EVENT_ID + "," + UPDATED_EVENT_ID);

        // Get all the budgetList where eventId equals to UPDATED_EVENT_ID
        defaultBudgetShouldNotBeFound("eventId.in=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllBudgetsByEventIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where eventId is not null
        defaultBudgetShouldBeFound(savedBudget, "eventId.specified=true");

        // Get all the budgetList where eventId is null
        defaultBudgetShouldNotBeFound("eventId.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetsByEventIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where eventId is greater than or equal to DEFAULT_EVENT_ID
        defaultBudgetShouldBeFound(savedBudget, "eventId.greaterThanOrEqual=" + DEFAULT_EVENT_ID);

        // Get all the budgetList where eventId is greater than or equal to UPDATED_EVENT_ID
        defaultBudgetShouldNotBeFound("eventId.greaterThanOrEqual=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllBudgetsByEventIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where eventId is less than or equal to DEFAULT_EVENT_ID
        defaultBudgetShouldBeFound(savedBudget, "eventId.lessThanOrEqual=" + DEFAULT_EVENT_ID);

        // Get all the budgetList where eventId is less than or equal to SMALLER_EVENT_ID
        defaultBudgetShouldNotBeFound("eventId.lessThanOrEqual=" + SMALLER_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllBudgetsByEventIdIsLessThanSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where eventId is less than DEFAULT_EVENT_ID
        defaultBudgetShouldNotBeFound("eventId.lessThan=" + DEFAULT_EVENT_ID);

        // Get all the budgetList where eventId is less than UPDATED_EVENT_ID
        defaultBudgetShouldBeFound(savedBudget, "eventId.lessThan=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllBudgetsByEventIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where eventId is greater than DEFAULT_EVENT_ID
        defaultBudgetShouldNotBeFound("eventId.greaterThan=" + DEFAULT_EVENT_ID);

        // Get all the budgetList where eventId is greater than SMALLER_EVENT_ID
        defaultBudgetShouldBeFound(savedBudget, "eventId.greaterThan=" + SMALLER_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllBudgetsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where amount equals to DEFAULT_AMOUNT
        defaultBudgetShouldBeFound(savedBudget, "amount.equals=" + DEFAULT_AMOUNT);

        // Get all the budgetList where amount equals to UPDATED_AMOUNT
        defaultBudgetShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetsByAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where amount not equals to DEFAULT_AMOUNT
        defaultBudgetShouldNotBeFound("amount.notEquals=" + DEFAULT_AMOUNT);

        // Get all the budgetList where amount not equals to UPDATED_AMOUNT
        defaultBudgetShouldBeFound(savedBudget, "amount.notEquals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultBudgetShouldBeFound(savedBudget, "amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the budgetList where amount equals to UPDATED_AMOUNT
        defaultBudgetShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where amount is not null
        defaultBudgetShouldBeFound(savedBudget, "amount.specified=true");

        // Get all the budgetList where amount is null
        defaultBudgetShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultBudgetShouldBeFound(savedBudget, "amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the budgetList where amount is greater than or equal to UPDATED_AMOUNT
        defaultBudgetShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where amount is less than or equal to DEFAULT_AMOUNT
        defaultBudgetShouldBeFound(savedBudget, "amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the budgetList where amount is less than or equal to SMALLER_AMOUNT
        defaultBudgetShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where amount is less than DEFAULT_AMOUNT
        defaultBudgetShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the budgetList where amount is less than UPDATED_AMOUNT
        defaultBudgetShouldBeFound(savedBudget, "amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where amount is greater than DEFAULT_AMOUNT
        defaultBudgetShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the budgetList where amount is greater than SMALLER_AMOUNT
        defaultBudgetShouldBeFound(savedBudget, "amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where type equals to DEFAULT_TYPE
        defaultBudgetShouldBeFound(savedBudget, "type.equals=" + DEFAULT_TYPE);

        // Get all the budgetList where type equals to UPDATED_TYPE
        defaultBudgetShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllBudgetsByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where type not equals to DEFAULT_TYPE
        defaultBudgetShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the budgetList where type not equals to UPDATED_TYPE
        defaultBudgetShouldBeFound(savedBudget, "type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllBudgetsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultBudgetShouldBeFound(savedBudget, "type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the budgetList where type equals to UPDATED_TYPE
        defaultBudgetShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllBudgetsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where type is not null
        defaultBudgetShouldBeFound(savedBudget, "type.specified=true");

        // Get all the budgetList where type is null
        defaultBudgetShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where name equals to DEFAULT_NAME
        defaultBudgetShouldBeFound(savedBudget, "name.equals=" + DEFAULT_NAME);

        // Get all the budgetList where name equals to UPDATED_NAME
        defaultBudgetShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBudgetsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where name not equals to DEFAULT_NAME
        defaultBudgetShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the budgetList where name not equals to UPDATED_NAME
        defaultBudgetShouldBeFound(savedBudget, "name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBudgetsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBudgetShouldBeFound(savedBudget, "name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the budgetList where name equals to UPDATED_NAME
        defaultBudgetShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBudgetsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where name is not null
        defaultBudgetShouldBeFound(savedBudget, "name.specified=true");

        // Get all the budgetList where name is null
        defaultBudgetShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetsByNameContainsSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where name contains DEFAULT_NAME
        defaultBudgetShouldBeFound(savedBudget, "name.contains=" + DEFAULT_NAME);

        // Get all the budgetList where name contains UPDATED_NAME
        defaultBudgetShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBudgetsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        Budget budget = createBudgetEntity();
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList where name does not contain DEFAULT_NAME
        defaultBudgetShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the budgetList where name does not contain UPDATED_NAME
        defaultBudgetShouldBeFound(savedBudget, "name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBudgetShouldBeFound(Budget budget, String filter) throws Exception {
        restBudgetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budget.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));

        // Check, that the count call also returns 1
        restBudgetMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBudgetShouldNotBeFound(String filter) throws Exception {
        restBudgetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBudgetMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("0"));
    }


    @Test
    @WithCurrentCCAdministrator
    public void getBudget_WithCurrentCCAdministrator() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        Budget savedBudget = initBudgetDB(budget);

        restBudgetMockMvc.perform(
            get(
                "/api/event-budget/{id}/event/{eventId}",
                savedBudget.getId(),
                savedEvent.getId()
            ))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(savedBudget.getId().intValue()))
            .andExpect(jsonPath("$.eventId").value(savedEvent.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS.toString()));
    }

    @Test
    @WithEventCrew
    public void getBudget_WithEventCrew() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEventCrew.getEventId());
        Budget savedBudget = initBudgetDB(budget);

        restBudgetMockMvc.perform(
            get(
                "/api/event-budget/{id}/event/{eventId}",
                savedBudget.getId(),
                savedEventCrew.getEventId()
            ))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(savedBudget.getId().intValue()))
            .andExpect(jsonPath("$.eventId").value(savedEventCrew.getEventId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS.toString()));
    }

    @Test
    @WithMockUser
    public void getBudget_WithMockUser() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        Budget savedBudget = initBudgetDB(budget);

        restBudgetMockMvc.perform(
            get(
                "/api/event-budget/{id}/event/{eventId}",
                savedBudget.getId(),
                savedEvent.getId()
            ))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithCurrentCCAdministrator
    public void getBudget_WithBudgetNotExist_ShouldThrow400() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        restBudgetMockMvc.perform(
            get(
                "/api/event-budget/{id}/event/{eventId}",
                Long.MAX_VALUE,
                savedEvent.getId()
            ).accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithCurrentCCAdministrator
    public void getBudget_WithWrongEventId_ShouldThrow404() throws Exception {

        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(Long.MAX_VALUE);
        Budget savedBudget = initBudgetDB(budget);

        assertThat(savedBudget.getEventId()).isNotEqualTo(savedEvent.getId());

        restBudgetMockMvc.perform(
            get(
                "/api/event-budget/{id}/event/{eventId}",
                savedBudget.getId(),
                savedEvent.getId()
            ).accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateBudget_WithCurrentCCAdministrator() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        Budget savedBudget = createUpdateBudgetEntity();
        savedBudget.setEventId(savedEvent.getId());
        savedBudget = initBudgetDB(savedBudget);

        int databaseSizeBeforeUpdate = budgetRepository.findAll().size();

        BudgetDTO budgetDTO = budgetMapper.toDto(savedBudget);

        restBudgetMockMvc.perform(put("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isOk());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeUpdate);
        Budget testBudget = budgetList.get(budgetList.size() - 1);
        assertThat(testBudget.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testBudget.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testBudget.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBudget.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBudget.getDetails()).isEqualTo(UPDATED_DETAILS);
    }

    @Test
    @WithEventHead
    public void updateBudget_WithEventHead() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        Budget savedBudget = createUpdateBudgetEntity();
        savedBudget.setEventId(savedEventCrew.getEventId());
        savedBudget = initBudgetDB(savedBudget);

        int databaseSizeBeforeUpdate = budgetRepository.findAll().size();

        BudgetDTO budgetDTO = budgetMapper.toDto(savedBudget);

        restBudgetMockMvc.perform(put("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isOk());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeUpdate);
        Budget testBudget = budgetList.get(budgetList.size() - 1);
        assertThat(testBudget.getEventId()).isEqualTo(savedEventCrew.getEventId());
        assertThat(testBudget.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testBudget.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBudget.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBudget.getDetails()).isEqualTo(UPDATED_DETAILS);
    }

    @Test
    @WithEventCrew
    public void updateBudget_WithEventCrew_ShouldThrow403() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        Budget savedBudget = createUpdateBudgetEntity();
        savedBudget.setEventId(savedEventCrew.getEventId());
        savedBudget = initBudgetDB(savedBudget);

        BudgetDTO budgetDTO = budgetMapper.toDto(savedBudget);

        restBudgetMockMvc.perform(put("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateBudget_WithBudgetNotExist_ShouldThrow400() throws Exception {
        int databaseSizeBeforeUpdate = budgetRepository.findAll().size();

        Event savedEvent = initEventDB(createEventEntity());
        Budget savedBudget = createUpdateBudgetEntity();
        savedBudget.setEventId(savedEvent.getId());
        BudgetDTO budgetDTO = budgetMapper.toDto(savedBudget);

        restBudgetMockMvc.perform(put("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isBadRequest());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateBudget_WithEventCancelled_ShouldThrow400() throws Exception {

        Event event = createEventEntity();
        event.setStatus(EventStatus.CANCELLED);
        Event savedEvent = initEventDB(event);
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        Budget savedBudget = initBudgetDB(budget);
        BudgetDTO budgetDTO = budgetMapper.toDto(savedBudget);

        int databaseSizeBeforeUpdate = budgetRepository.findAll().size();

        assertThat(savedEvent.getStatus()).isEqualByComparingTo(EventStatus.CANCELLED);
        assertThat(savedBudget.getEventId()).isEqualTo(savedEvent.getId());

        restBudgetMockMvc.perform(put("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isBadRequest());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateBudget_WithEventEnded_ShouldThrow400() throws Exception {

        Event event = createEventEntity();
        event.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS));
        Event savedEvent = initEventDB(event);
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        Budget savedBudget = initBudgetDB(budget);
        BudgetDTO budgetDTO = budgetMapper.toDto(savedBudget);

        int databaseSizeBeforeUpdate = budgetRepository.findAll().size();

        assertThat(savedEvent.getEndDate()).isBefore(Instant.now());
        assertThat(savedBudget.getEventId()).isEqualTo(savedEvent.getId());

        restBudgetMockMvc.perform(put("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isBadRequest());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void getTotalBudgetByEventId_WithCurrentCCAdministrator() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        EventBudgetTotalDTO eventBudgetTotalDTO = new EventBudgetTotalDTO();
        for (int i = 0; i < 2; i++) {
            Budget budget = createBudgetEntity();
            budget.setEventId(savedEvent.getId());
            Budget savedBudget = initBudgetDB(budget);
            eventBudgetTotalDTO.addTotalIncome(savedBudget.getAmount());
        }
        for (int i = 0; i < 2; i++) {
            Budget budget = createBudgetEntity();
            budget.setEventId(savedEvent.getId());
            budget.setType(UPDATED_TYPE);
            Budget savedBudget = initBudgetDB(budget);
            eventBudgetTotalDTO.addTotalExpense(savedBudget.getAmount());
        }

        restBudgetMockMvc.perform(get("/api/event-budget/event/{eventId}/total", savedEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.totalExpense").value(eventBudgetTotalDTO.getTotalExpense()))
            .andExpect(jsonPath("$.totalIncome").value(eventBudgetTotalDTO.getTotalIncome()));
    }

    @Test
    @WithEventCrew
    public void getTotalBudgetByEventId_WithEventCrew() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        EventBudgetTotalDTO eventBudgetTotalDTO = new EventBudgetTotalDTO();
        for (int i = 0; i < 2; i++) {
            Budget budget = createBudgetEntity();
            budget.setEventId(savedEventCrew.getEventId());
            Budget savedBudget = initBudgetDB(budget);
            eventBudgetTotalDTO.addTotalIncome(savedBudget.getAmount());
        }
        for (int i = 0; i < 2; i++) {
            Budget budget = createBudgetEntity();
            budget.setEventId(savedEventCrew.getEventId());
            budget.setType(UPDATED_TYPE);
            Budget savedBudget = initBudgetDB(budget);
            eventBudgetTotalDTO.addTotalExpense(savedBudget.getAmount());
        }

        restBudgetMockMvc.perform(get("/api/event-budget/event/{eventId}/total", savedEventCrew.getEventId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.totalExpense").value(eventBudgetTotalDTO.getTotalExpense()))
            .andExpect(jsonPath("$.totalIncome").value(eventBudgetTotalDTO.getTotalIncome()));
    }

    @Test
    @WithEventCrew
    public void getTotalBudgetByEventId_WithoutBudget() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        EventBudgetTotalDTO eventBudgetTotalDTO = new EventBudgetTotalDTO();

        restBudgetMockMvc.perform(get("/api/event-budget/event/{eventId}/total", savedEventCrew.getEventId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.totalExpense").value(eventBudgetTotalDTO.getTotalExpense()))
            .andExpect(jsonPath("$.totalIncome").value(eventBudgetTotalDTO.getTotalIncome()));
    }

    @Test
    @WithMockUser
    public void getTotalBudgetByEventId_WithNormalUser_ShouldThrow403() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        EventBudgetTotalDTO eventBudgetTotalDTO = new EventBudgetTotalDTO();
        for (int i = 0; i < 2; i++) {
            Budget budget = createBudgetEntity();
            budget.setEventId(savedEvent.getId());
            Budget savedBudget = initBudgetDB(budget);
            eventBudgetTotalDTO.addTotalIncome(savedBudget.getAmount());
        }
        for (int i = 0; i < 2; i++) {
            Budget budget = createBudgetEntity();
            budget.setEventId(savedEvent.getId());
            budget.setType(UPDATED_TYPE);
            Budget savedBudget = initBudgetDB(budget);
            eventBudgetTotalDTO.addTotalExpense(savedBudget.getAmount());
        }

        restBudgetMockMvc.perform(get("/api/event-budget/event/{eventId}/total", savedEvent.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithCurrentCCAdministrator
    public void deleteBudget_WithCurrentCCAdministrator() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        Budget savedBudget = initBudgetDB(budget);

        int databaseSizeBeforeDelete = budgetRepository.findAll().size();

        restBudgetMockMvc.perform(delete(
            "/api/event-budget/{id}/event/{eventId}",
            savedBudget.getId(),
            savedEvent.getId()
        ).accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @WithEventHead
    public void deleteBudget_WithEventHead() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEventCrew.getEventId());
        Budget savedBudget = initBudgetDB(budget);

        int databaseSizeBeforeDelete = budgetRepository.findAll().size();

        restBudgetMockMvc.perform(
            delete(
                "/api/event-budget/{id}/event/{eventId}",
                savedBudget.getId(),
                savedEventCrew.getEventId()
            ).accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @WithEventCrew
    public void deleteBudget_WithEventCrew_ShouldThrow403() throws Exception {
        EventCrew savedEventCrew = getEventCrewByCurrentLoginUser();
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEventCrew.getEventId());
        Budget savedBudget = initBudgetDB(budget);

        restBudgetMockMvc.perform(
            delete(
                "/api/event-budget/{id}/event/{eventId}",
                savedBudget.getId(),
                savedEventCrew.getEventId()
            ).accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithCurrentCCAdministrator
    public void deleteBudget_WithBudgetNotExist_ShouldThrow400() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        restBudgetMockMvc.perform(
            delete(
                "/api/event-budget/{id}/event/{eventId}",
                Long.MAX_VALUE,
                savedEvent.getId()
            ).accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithCurrentCCAdministrator
    public void deleteBudget_WithWrongEventId_ShouldThrow400() throws Exception {

        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(Long.MAX_VALUE);
        Budget savedBudget = initBudgetDB(budget);

        assertThat(savedBudget.getEventId()).isNotEqualTo(savedEvent.getId());

        restBudgetMockMvc.perform(
            delete(
                "/api/event-budget/{id}/event/{eventId}",
                savedBudget.getId(),
                savedEvent.getId()
            ).accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Budget.class);
        Budget budget1 = new Budget();
        budget1.setId(1L);
        Budget budget2 = new Budget();
        budget2.setId(budget1.getId());
        assertThat(budget1).isEqualTo(budget2);
        budget2.setId(2L);
        assertThat(budget1).isNotEqualTo(budget2);
        budget1.setId(null);
        assertThat(budget1).isNotEqualTo(budget2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BudgetDTO.class);
        BudgetDTO budgetDTO1 = new BudgetDTO();
        budgetDTO1.setId(1L);
        BudgetDTO budgetDTO2 = new BudgetDTO();
        assertThat(budgetDTO1).isNotEqualTo(budgetDTO2);
        budgetDTO2.setId(budgetDTO1.getId());
        assertThat(budgetDTO1).isEqualTo(budgetDTO2);
        budgetDTO2.setId(2L);
        assertThat(budgetDTO1).isNotEqualTo(budgetDTO2);
        budgetDTO1.setId(null);
        assertThat(budgetDTO1).isNotEqualTo(budgetDTO2);
    }

    @Test
    public void testEntityFromId() {
        assertThat(budgetMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(budgetMapper.fromId(null)).isNull();
    }

    private Event initEventDB(Event event) {
        return eventRepository.saveAndFlush(event);
    }

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

    private Budget initBudgetDB(Budget budget) {
        return budgetRepository.saveAndFlush(budget);
    }
}
