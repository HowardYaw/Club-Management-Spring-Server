package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Budget;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.repository.BudgetRepository;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.BudgetService;
import com.thirdcc.webapp.service.dto.BudgetDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thirdcc.webapp.domain.enumeration.TransactionType;

/**
 * Integration tests for the {@Link BudgetResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class BudgetResourceIT {

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(11, 2);
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
    private EventRepository eventRepository;

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
    public void createBudget() throws Exception {
        int databaseSizeBeforeCreate = budgetRepository.findAll().size();

        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        BudgetDTO budgetDTO = budgetMapper.toDto(budget);

        restBudgetMockMvc.perform(post("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isCreated());

        // Validate the Budget in the database
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
    public void createBudget_WithIdInDTO_ShouldThrow400() throws Exception {
        int databaseSizeBeforeCreate = budgetRepository.findAll().size();

        Budget budget = createBudgetEntity();
        budget.setId(1L);
        BudgetDTO budgetDTO = budgetMapper.toDto(budget);

        restBudgetMockMvc.perform(post("/api/event-budget")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Budget in the database
        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
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
    public void getAllBudgetsByEventId() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        Budget savedBudget = initBudgetDB(budget);

        // Get all the budgetList
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
    public void getBudget() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        Budget savedBudget = initBudgetDB(budget);

        restBudgetMockMvc.perform(get("/api/event-budget/{id}", savedBudget.getId()))
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
    public void getBudget_WithBudgetNotExist_ShouldThrow404() throws Exception {
        restBudgetMockMvc.perform(get("/api/event-budget/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateBudget() throws Exception {
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
    public void deleteBudget() throws Exception {
        Event savedEvent = initEventDB(createEventEntity());
        Budget budget = createBudgetEntity();
        budget.setEventId(savedEvent.getId());
        Budget savedBudget = initBudgetDB(budget);

        int databaseSizeBeforeDelete = budgetRepository.findAll().size();

        restBudgetMockMvc.perform(delete("/api/event-budget/{id}", savedBudget.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void deleteBudget_WithBudgetNotExist_ShouldThrow400() throws Exception {
        restBudgetMockMvc.perform(delete("/api/event-budget/{id}", Long.MAX_VALUE)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
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

    private Budget initBudgetDB(Budget budget) {
        return budgetRepository.saveAndFlush(budget);
    }
}
