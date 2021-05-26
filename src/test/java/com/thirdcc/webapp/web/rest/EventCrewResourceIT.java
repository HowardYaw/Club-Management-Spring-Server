package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.WithCurrentCCAdministrator;
import com.thirdcc.webapp.annotations.authorization.WithCurrentCCHead;
import com.thirdcc.webapp.annotations.authorization.WithEventCrew;
import com.thirdcc.webapp.annotations.authorization.WithEventHead;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.EventCrewRepository;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.EventCrewService;
import com.thirdcc.webapp.service.dto.EventCrewDTO;
import com.thirdcc.webapp.service.mapper.EventCrewMapper;

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
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thirdcc.webapp.domain.enumeration.EventCrewRole;

/**
 * Integration tests for the {@Link EventCrewResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@InitYearSession
public class EventCrewResourceIT {

    private static final String ENTITY_API_URL = "/api/event-crews";

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long SMALLER_USER_ID = DEFAULT_USER_ID - 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long SMALLER_EVENT_ID = DEFAULT_EVENT_ID - 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final EventCrewRole DEFAULT_ROLE = EventCrewRole.HEAD;
    private static final EventCrewRole UPDATED_ROLE = EventCrewRole.HEAD;

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
    private EventCrewRepository eventCrewRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private EventCrewMapper eventCrewMapper;

    @Autowired
    private EventCrewService eventCrewService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventCrewMockMvc;

    private EventCrew eventCrew;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    @AfterEach
    public void cleanUp() {
        eventRepository.deleteAll();
        eventCrewRepository.deleteAll();
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

    public static EventCrew createEventCrewEntity() {
        EventCrew eventCrew = new EventCrew()
            .userId(DEFAULT_USER_ID)
            .eventId(DEFAULT_EVENT_ID)
            .role(DEFAULT_ROLE);
        return eventCrew;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventCrew createUpdatedEntity(EntityManager em) {
        EventCrew eventCrew = new EventCrew()
            .userId(UPDATED_USER_ID)
            .eventId(UPDATED_EVENT_ID)
            .role(UPDATED_ROLE);
        return eventCrew;
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void createEventCrew() throws Exception {
        int databaseSizeBeforeCreate = eventCrewRepository.findAll().size();

        // Create the EventCrew
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew eventCrew = createEventCrewEntity();
        eventCrew.setEventId(savedEvent.getId());
        EventCrewDTO eventCrewDTO = eventCrewMapper.toDto(eventCrew);

        restEventCrewMockMvc.perform(post("/api/event-crews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventCrewDTO)))
            .andExpect(status().isCreated());

        // Validate the EventCrew in the database
        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeCreate + 1);
        EventCrew testEventCrew = eventCrewList.get(eventCrewList.size() - 1);
        assertThat(testEventCrew.getUserId()).isEqualTo(eventCrew.getUserId());
        assertThat(testEventCrew.getEventId()).isEqualTo(eventCrew.getEventId());
        assertThat(testEventCrew.getRole()).isEqualTo(eventCrew.getRole());
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void createEventCrewWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = eventCrewRepository.findAll().size();

        // Create the EventCrew with an existing ID
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew eventCrew = createEventCrewEntity();
        eventCrew.setEventId(savedEvent.getId());
        eventCrew.setId(1L);
        EventCrewDTO eventCrewDTO = eventCrewMapper.toDto(eventCrew);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventCrewMockMvc.perform(post("/api/event-crews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventCrewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventCrew in the database
        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @WithMockUser
    public void createEventCrew_AsMockUser_ShouldReturn403() throws Exception {
        int databaseSizeBeforeCreate = eventCrewRepository.findAll().size();

        // Create the EventCrew
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew eventCrew = createEventCrewEntity();
        eventCrew.setEventId(savedEvent.getId());
        EventCrewDTO eventCrewDTO = eventCrewMapper.toDto(eventCrew);

        restEventCrewMockMvc.perform(post("/api/event-crews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventCrewDTO)))
            .andExpect(status().isForbidden());

        // Validate the EventCrew in the database
        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void getAllEventCrews() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew savedEventCrew = createEventCrewEntity();
        savedEventCrew.setEventId(savedEvent.getId());
        savedEventCrew = initEventCrewDB(savedEventCrew);

        // Get all the eventCrewList
        restEventCrewMockMvc.perform(get("/api/event-crews?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedEventCrew.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(savedEventCrew.getUserId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(savedEventCrew.getEventId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }

    @Test
    @Transactional
    void getEventCrewsByIdFiltering() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        Long id = eventCrew.getId();

        defaultEventCrewShouldBeFound("id.equals=" + id);
        defaultEventCrewShouldNotBeFound("id.notEquals=" + id);

        defaultEventCrewShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEventCrewShouldNotBeFound("id.greaterThan=" + id);

        defaultEventCrewShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEventCrewShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventCrewsByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where userId equals to DEFAULT_USER_ID
        defaultEventCrewShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the eventCrewList where userId equals to UPDATED_USER_ID
        defaultEventCrewShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByUserIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where userId not equals to DEFAULT_USER_ID
        defaultEventCrewShouldNotBeFound("userId.notEquals=" + DEFAULT_USER_ID);

        // Get all the eventCrewList where userId not equals to UPDATED_USER_ID
        defaultEventCrewShouldBeFound("userId.notEquals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultEventCrewShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the eventCrewList where userId equals to UPDATED_USER_ID
        defaultEventCrewShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where userId is not null
        defaultEventCrewShouldBeFound("userId.specified=true");

        // Get all the eventCrewList where userId is null
        defaultEventCrewShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    void getAllEventCrewsByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where userId is greater than or equal to DEFAULT_USER_ID
        defaultEventCrewShouldBeFound("userId.greaterThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the eventCrewList where userId is greater than or equal to UPDATED_USER_ID
        defaultEventCrewShouldNotBeFound("userId.greaterThanOrEqual=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByUserIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where userId is less than or equal to DEFAULT_USER_ID
        defaultEventCrewShouldBeFound("userId.lessThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the eventCrewList where userId is less than or equal to SMALLER_USER_ID
        defaultEventCrewShouldNotBeFound("userId.lessThanOrEqual=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where userId is less than DEFAULT_USER_ID
        defaultEventCrewShouldNotBeFound("userId.lessThan=" + DEFAULT_USER_ID);

        // Get all the eventCrewList where userId is less than UPDATED_USER_ID
        defaultEventCrewShouldBeFound("userId.lessThan=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByUserIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where userId is greater than DEFAULT_USER_ID
        defaultEventCrewShouldNotBeFound("userId.greaterThan=" + DEFAULT_USER_ID);

        // Get all the eventCrewList where userId is greater than SMALLER_USER_ID
        defaultEventCrewShouldBeFound("userId.greaterThan=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByEventIdIsEqualToSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where eventId equals to DEFAULT_EVENT_ID
        defaultEventCrewShouldBeFound("eventId.equals=" + DEFAULT_EVENT_ID);

        // Get all the eventCrewList where eventId equals to UPDATED_EVENT_ID
        defaultEventCrewShouldNotBeFound("eventId.equals=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByEventIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where eventId not equals to DEFAULT_EVENT_ID
        defaultEventCrewShouldNotBeFound("eventId.notEquals=" + DEFAULT_EVENT_ID);

        // Get all the eventCrewList where eventId not equals to UPDATED_EVENT_ID
        defaultEventCrewShouldBeFound("eventId.notEquals=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByEventIdIsInShouldWork() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where eventId in DEFAULT_EVENT_ID or UPDATED_EVENT_ID
        defaultEventCrewShouldBeFound("eventId.in=" + DEFAULT_EVENT_ID + "," + UPDATED_EVENT_ID);

        // Get all the eventCrewList where eventId equals to UPDATED_EVENT_ID
        defaultEventCrewShouldNotBeFound("eventId.in=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByEventIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where eventId is not null
        defaultEventCrewShouldBeFound("eventId.specified=true");

        // Get all the eventCrewList where eventId is null
        defaultEventCrewShouldNotBeFound("eventId.specified=false");
    }

    @Test
    @Transactional
    void getAllEventCrewsByEventIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where eventId is greater than or equal to DEFAULT_EVENT_ID
        defaultEventCrewShouldBeFound("eventId.greaterThanOrEqual=" + DEFAULT_EVENT_ID);

        // Get all the eventCrewList where eventId is greater than or equal to UPDATED_EVENT_ID
        defaultEventCrewShouldNotBeFound("eventId.greaterThanOrEqual=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByEventIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where eventId is less than or equal to DEFAULT_EVENT_ID
        defaultEventCrewShouldBeFound("eventId.lessThanOrEqual=" + DEFAULT_EVENT_ID);

        // Get all the eventCrewList where eventId is less than or equal to SMALLER_EVENT_ID
        defaultEventCrewShouldNotBeFound("eventId.lessThanOrEqual=" + SMALLER_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByEventIdIsLessThanSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where eventId is less than DEFAULT_EVENT_ID
        defaultEventCrewShouldNotBeFound("eventId.lessThan=" + DEFAULT_EVENT_ID);

        // Get all the eventCrewList where eventId is less than UPDATED_EVENT_ID
        defaultEventCrewShouldBeFound("eventId.lessThan=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByEventIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where eventId is greater than DEFAULT_EVENT_ID
        defaultEventCrewShouldNotBeFound("eventId.greaterThan=" + DEFAULT_EVENT_ID);

        // Get all the eventCrewList where eventId is greater than SMALLER_EVENT_ID
        defaultEventCrewShouldBeFound("eventId.greaterThan=" + SMALLER_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventCrewsByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where role equals to DEFAULT_ROLE
        defaultEventCrewShouldBeFound("role.equals=" + DEFAULT_ROLE);

        // Get all the eventCrewList where role equals to UPDATED_ROLE
        defaultEventCrewShouldNotBeFound("role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllEventCrewsByRoleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where role not equals to DEFAULT_ROLE
        defaultEventCrewShouldNotBeFound("role.notEquals=" + DEFAULT_ROLE);

        // Get all the eventCrewList where role not equals to UPDATED_ROLE
        defaultEventCrewShouldBeFound("role.notEquals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllEventCrewsByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where role in DEFAULT_ROLE or UPDATED_ROLE
        defaultEventCrewShouldBeFound("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE);

        // Get all the eventCrewList where role equals to UPDATED_ROLE
        defaultEventCrewShouldNotBeFound("role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllEventCrewsByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList where role is not null
        defaultEventCrewShouldBeFound("role.specified=true");

        // Get all the eventCrewList where role is null
        defaultEventCrewShouldNotBeFound("role.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventCrewShouldBeFound(String filter) throws Exception {
        restEventCrewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventCrew.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));

        // Check, that the count call also returns 1
        restEventCrewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventCrewShouldNotBeFound(String filter) throws Exception {
        restEventCrewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventCrewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void getEventCrew() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew savedEventCrew = createEventCrewEntity();
        savedEventCrew.setEventId(savedEvent.getId());
        savedEventCrew = initEventCrewDB(savedEventCrew);

        // Get the eventCrew
        restEventCrewMockMvc.perform(get("/api/event-crews/{id}?eventId={eventId}", savedEventCrew.getId(), savedEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(savedEventCrew.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(savedEventCrew.getUserId().intValue()))
            .andExpect(jsonPath("$.eventId").value(savedEventCrew.getEventId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()));
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void getEventCrew_WithNonExistingEventCrew_ShouldReturn404() throws Exception {
        // Get the eventCrew
        restEventCrewMockMvc.perform(get("/api/event-crews/{id}?eventId={eventId}", Long.MAX_VALUE, DEFAULT_EVENT_ID))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser
    public void getEventCrew_AsMockUser_ShouldReturn403() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew savedEventCrew = createEventCrewEntity();
        savedEventCrew.setEventId(savedEvent.getId());
        savedEventCrew = initEventCrewDB(savedEventCrew);

        // Get the eventCrew
        restEventCrewMockMvc.perform(get("/api/event-crews/{id}?eventId={eventId}", savedEventCrew.getId(), savedEvent.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithEventCrew
    public void getEventCrewWithEventId() throws Exception {
        // Initialize the database
        EventCrew tester = getEventCrewByCurrentLoginUser();

        EventCrew eventCrew = createEventCrewEntity();
        eventCrew.setEventId(tester.getEventId());
        EventCrew savedEventCrew = initEventCrewDB(eventCrew);

        // Get all the eventCrewList
        restEventCrewMockMvc.perform(get("/api/event-crews/event/{eventId}", tester.getEventId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedEventCrew.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(savedEventCrew.getUserId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(savedEventCrew.getEventId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }

    @Test
    @Transactional
    @WithMockUser
    public void getEventCrewWithEventId_WithMockUser_ShouldReturn403() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew eventCrew = createEventCrewEntity();
        eventCrew.setEventId(savedEvent.getId());
        EventCrew savedEventCrew = initEventCrewDB(eventCrew);

        // Get all the eventCrewList
        restEventCrewMockMvc.perform(get("/api/event-crews/event/{eventId}", savedEvent.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void updateEventCrew() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew savedEventCrew = createEventCrewEntity();
        savedEventCrew.setEventId(savedEvent.getId());
        savedEventCrew = initEventCrewDB(savedEventCrew);

        int databaseSizeBeforeUpdate = eventCrewRepository.findAll().size();

        EventCrewDTO eventCrewDTO = eventCrewMapper.toDto(savedEventCrew);

        restEventCrewMockMvc.perform(put("/api/event-crews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventCrewDTO)))
            .andExpect(status().isOk());

        // Validate the EventCrew in the database
        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeUpdate);
        EventCrew testEventCrew = eventCrewList.get(eventCrewList.size() - 1);
        assertThat(testEventCrew.getUserId()).isEqualTo(savedEventCrew.getUserId());
        assertThat(testEventCrew.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testEventCrew.getRole()).isEqualTo(UPDATED_ROLE);
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void updateNonExistingEventCrew_ShouldThrow400() throws Exception {
        int databaseSizeBeforeUpdate = eventCrewRepository.findAll().size();

        // Create the EventCrew
        EventCrew eventCrew = createEventCrewEntity();
        EventCrewDTO eventCrewDTO = eventCrewMapper.toDto(eventCrew);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventCrewMockMvc.perform(put("/api/event-crews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventCrewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventCrew in the database
        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser
    public void updateEventCrew_AsMockUser_ShouldThrow403() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew savedEventCrew = createEventCrewEntity();
        savedEventCrew.setEventId(savedEvent.getId());
        savedEventCrew = initEventCrewDB(savedEventCrew);

        EventCrewDTO eventCrewDTO = eventCrewMapper.toDto(savedEventCrew);

        restEventCrewMockMvc.perform(put("/api/event-crews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventCrewDTO)))
            .andExpect(status().isForbidden());

    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void deleteEventCrew() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew savedEventCrew = createEventCrewEntity();
        savedEventCrew.setEventId(savedEvent.getId());
        savedEventCrew = initEventCrewDB(savedEventCrew);

        int databaseSizeBeforeDelete = eventCrewRepository.findAll().size();

        // Delete the eventCrew
        restEventCrewMockMvc.perform(delete("/api/event-crews/{id}?eventId={eventId}", savedEventCrew.getId(), savedEvent.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void deleteEventCrew_WithNonExistingEventCrew_ShouldThrow400() throws Exception {
        int databaseSizeBeforeDelete = eventCrewRepository.findAll().size();

        // Delete the eventCrew
        restEventCrewMockMvc.perform(delete("/api/event-crews/{id}", Long.MAX_VALUE, DEFAULT_EVENT_ID)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    @WithEventCrew
    public void deleteEventCrew_AsEventCrew_ShouldThrow403() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB(createEventEntity());
        EventCrew savedEventCrew = createEventCrewEntity();
        savedEventCrew.setEventId(savedEvent.getId());
        savedEventCrew = initEventCrewDB(savedEventCrew);

        int databaseSizeBeforeDelete = eventCrewRepository.findAll().size();

        // Delete the eventCrew
        restEventCrewMockMvc.perform(delete("/api/event-crews/{id}?eventId={eventId}", savedEventCrew.getId(), savedEvent.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        // Validate the database contains one less item
        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventCrew.class);
        EventCrew eventCrew1 = new EventCrew();
        eventCrew1.setId(1L);
        EventCrew eventCrew2 = new EventCrew();
        eventCrew2.setId(eventCrew1.getId());
        assertThat(eventCrew1).isEqualTo(eventCrew2);
        eventCrew2.setId(2L);
        assertThat(eventCrew1).isNotEqualTo(eventCrew2);
        eventCrew1.setId(null);
        assertThat(eventCrew1).isNotEqualTo(eventCrew2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventCrewDTO.class);
        EventCrewDTO eventCrewDTO1 = new EventCrewDTO();
        eventCrewDTO1.setId(1L);
        EventCrewDTO eventCrewDTO2 = new EventCrewDTO();
        assertThat(eventCrewDTO1).isNotEqualTo(eventCrewDTO2);
        eventCrewDTO2.setId(eventCrewDTO1.getId());
        assertThat(eventCrewDTO1).isEqualTo(eventCrewDTO2);
        eventCrewDTO2.setId(2L);
        assertThat(eventCrewDTO1).isNotEqualTo(eventCrewDTO2);
        eventCrewDTO1.setId(null);
        assertThat(eventCrewDTO1).isNotEqualTo(eventCrewDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(eventCrewMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(eventCrewMapper.fromId(null)).isNull();
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

    private EventCrew initEventCrewDB(EventCrew eventCrew) {
        return eventCrewRepository.saveAndFlush(eventCrew);
    }
}
