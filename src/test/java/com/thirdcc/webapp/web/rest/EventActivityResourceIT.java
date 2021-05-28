package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.WithCurrentCCAdministrator;
import com.thirdcc.webapp.annotations.authorization.WithNormalUser;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventActivity;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.repository.EventActivityRepository;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.EventActivityService;
import com.thirdcc.webapp.service.dto.EventActivityDTO;
import com.thirdcc.webapp.service.mapper.EventActivityMapper;

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

import static com.thirdcc.webapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.BIG_DECIMAL;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link EventActivityResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@InitYearSession
@WithNormalUser
public class EventActivityResourceIT {

    private static final String ENTITY_API_URL = "/api/event-activities";

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long SMALLER_EVENT_ID = DEFAULT_EVENT_ID - 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final Instant DEFAULT_START_DATE = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private static final Instant UPDATED_START_DATE = Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_DURATION_IN_DAY = new BigDecimal(1);
    private static final BigDecimal SMALLER_DURATION_IN_DAY = new BigDecimal(1).subtract(BigDecimal.ONE);
    private static final BigDecimal UPDATED_DURATION_IN_DAY = new BigDecimal(2);

    private static final String DEFAULT_NAME = "DEFAULT_NAME";
    private static final String UPDATED_NAME = "UPDATED_NAME";

    private static final String DEFAULT_DESCRIPTION = "DEFAULT_DESCRIPTION";
    private static final String UPDATED_DESCRIPTION = "UPDATED_DESCRIPTION";

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
    private EventActivityRepository eventActivityRepository;

    @Autowired
    private EventActivityMapper eventActivityMapper;

    @Autowired
    private EventActivityService eventActivityService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventActivityMockMvc;

    private EventActivity eventActivity;

    private Event event;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    public static EventActivity createEventActivityEntity(EntityManager em) {
        EventActivity eventActivity = new EventActivity()
            .eventId(DEFAULT_EVENT_ID)
            .startDate(DEFAULT_START_DATE)
            .durationInDay(DEFAULT_DURATION_IN_DAY)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
        return eventActivity;
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

    public static EventActivityDTO createDefaultEventActivityDTO() {
        EventActivityDTO eventActivityDTO = new EventActivityDTO();
        eventActivityDTO.setDescription(DEFAULT_DESCRIPTION);
        eventActivityDTO.setDurationInDay(DEFAULT_DURATION_IN_DAY);
        eventActivityDTO.setName(DEFAULT_NAME);
        eventActivityDTO.setStartDate(DEFAULT_START_DATE);
        return eventActivityDTO;
    }

    public static EventActivityDTO createUpdateEventActivityDTO() {
        EventActivityDTO eventActivityDTO = new EventActivityDTO();
        eventActivityDTO.setEventId(Long.MAX_VALUE);
        eventActivityDTO.setDescription(UPDATED_DESCRIPTION);
        eventActivityDTO.setDurationInDay(UPDATED_DURATION_IN_DAY);
        eventActivityDTO.setName(UPDATED_NAME);
        eventActivityDTO.setStartDate(UPDATED_START_DATE);
        return eventActivityDTO;
    }

    @BeforeEach
    public void initTest() {
        event = createEventEntity(em);
        eventActivity = createEventActivityEntity(em);
    }

    @AfterEach
    public void cleanUp() {
        eventRepository.deleteAll();
        eventActivityRepository.deleteAll();
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventActivity() throws Exception {
        Event savedEvent = initEventDB();

        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createDefaultEventActivityDTO();
        eventActivityDTO.setEventId(savedEvent.getId());

        restEventActivityMockMvc.perform(post("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isCreated());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate + 1);
        EventActivity testEventActivity = eventActivityList.get(eventActivityList.size() - 1);
        assertThat(testEventActivity.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testEventActivity.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testEventActivity.getDurationInDay()).isEqualTo(DEFAULT_DURATION_IN_DAY.setScale(2));
        assertThat(testEventActivity.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEventActivity.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventActivity_WithEventEnded_ShouldThrow400() throws Exception {
        event.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS));
        Event savedEvent = initEventDB();

        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        assertThat(savedEvent.getEndDate()).isBefore(Instant.now());

        EventActivityDTO eventActivityDTO = createDefaultEventActivityDTO();
        eventActivityDTO.setEventId(savedEvent.getId());

        restEventActivityMockMvc.perform(post("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventActivity_WithEventClosed_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CLOSED);
        Event savedEvent = initEventDB();

        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        assertThat(savedEvent.getStatus()).isEqualByComparingTo(EventStatus.CLOSED);

        EventActivityDTO eventActivityDTO = createDefaultEventActivityDTO();
        eventActivityDTO.setEventId(savedEvent.getId());

        restEventActivityMockMvc.perform(post("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventActivity_WithEventCancelled_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CANCELLED);
        Event savedEvent = initEventDB();

        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        assertThat(savedEvent.getStatus()).isEqualByComparingTo(EventStatus.CANCELLED);

        EventActivityDTO eventActivityDTO = createDefaultEventActivityDTO();
        eventActivityDTO.setEventId(savedEvent.getId());

        restEventActivityMockMvc.perform(post("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventActivity_WithEventActivityStartDateEarlierThanToday_ShouldThrow400() throws Exception {
        Event savedEvent = initEventDB();

        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createDefaultEventActivityDTO();
        eventActivityDTO.setEventId(savedEvent.getId());
        eventActivityDTO.setStartDate(Instant.now().minus(1, ChronoUnit.SECONDS));

        assertThat(eventActivityDTO.getStartDate()).isBefore(Instant.now());

        restEventActivityMockMvc.perform(post("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventActivity_WithEventActivityStartDateLaterThanEventEndDate_ShouldThrow400() throws Exception {
        Event savedEvent = initEventDB();

        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createDefaultEventActivityDTO();
        eventActivityDTO.setEventId(savedEvent.getId());
        eventActivityDTO.setStartDate(savedEvent.getEndDate().plus(1, ChronoUnit.SECONDS));

        assertThat(eventActivityDTO.getStartDate()).isAfter(savedEvent.getEndDate());

        restEventActivityMockMvc.perform(post("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventActivityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        // Create the EventActivity with an existing ID
        eventActivity.setId(1L);
        EventActivityDTO eventActivityDTO = eventActivityMapper.toDto(eventActivity);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventActivityMockMvc.perform(post("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventActivity in the database
        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @WithCurrentCCAdministrator
    public void getAllEventActivities() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        // Get all the eventActivityList
        restEventActivityMockMvc.perform(get("/api/event-activities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedEventActivity.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(savedEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].durationInDay").value(hasItem(DEFAULT_DURATION_IN_DAY.doubleValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @WithCurrentCCAdministrator
    public void getAllEventActivitiesByEventId() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        // Get all the eventActivityList
        restEventActivityMockMvc.perform(get("/api/event-activities/event/{eventId}?sort=id,desc", savedEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedEventActivity.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(savedEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].durationInDay").value(hasItem(DEFAULT_DURATION_IN_DAY.doubleValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    void getEventActivitiesByIdFiltering() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        Long id = eventActivity.getId();

        defaultEventActivityShouldBeFound("id.equals=" + id);
        defaultEventActivityShouldNotBeFound("id.notEquals=" + id);

        defaultEventActivityShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEventActivityShouldNotBeFound("id.greaterThan=" + id);

        defaultEventActivityShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEventActivityShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByEventIdIsEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where eventId equals to DEFAULT_EVENT_ID
        defaultEventActivityShouldBeFound("eventId.equals=" + DEFAULT_EVENT_ID);

        // Get all the eventActivityList where eventId equals to UPDATED_EVENT_ID
        defaultEventActivityShouldNotBeFound("eventId.equals=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByEventIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where eventId not equals to DEFAULT_EVENT_ID
        defaultEventActivityShouldNotBeFound("eventId.notEquals=" + DEFAULT_EVENT_ID);

        // Get all the eventActivityList where eventId not equals to UPDATED_EVENT_ID
        defaultEventActivityShouldBeFound("eventId.notEquals=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByEventIdIsInShouldWork() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where eventId in DEFAULT_EVENT_ID or UPDATED_EVENT_ID
        defaultEventActivityShouldBeFound("eventId.in=" + DEFAULT_EVENT_ID + "," + UPDATED_EVENT_ID);

        // Get all the eventActivityList where eventId equals to UPDATED_EVENT_ID
        defaultEventActivityShouldNotBeFound("eventId.in=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByEventIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where eventId is not null
        defaultEventActivityShouldBeFound("eventId.specified=true");

        // Get all the eventActivityList where eventId is null
        defaultEventActivityShouldNotBeFound("eventId.specified=false");
    }

    @Test
    @Transactional
    void getAllEventActivitiesByEventIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where eventId is greater than or equal to DEFAULT_EVENT_ID
        defaultEventActivityShouldBeFound("eventId.greaterThanOrEqual=" + DEFAULT_EVENT_ID);

        // Get all the eventActivityList where eventId is greater than or equal to UPDATED_EVENT_ID
        defaultEventActivityShouldNotBeFound("eventId.greaterThanOrEqual=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByEventIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where eventId is less than or equal to DEFAULT_EVENT_ID
        defaultEventActivityShouldBeFound("eventId.lessThanOrEqual=" + DEFAULT_EVENT_ID);

        // Get all the eventActivityList where eventId is less than or equal to SMALLER_EVENT_ID
        defaultEventActivityShouldNotBeFound("eventId.lessThanOrEqual=" + SMALLER_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByEventIdIsLessThanSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where eventId is less than DEFAULT_EVENT_ID
        defaultEventActivityShouldNotBeFound("eventId.lessThan=" + DEFAULT_EVENT_ID);

        // Get all the eventActivityList where eventId is less than UPDATED_EVENT_ID
        defaultEventActivityShouldBeFound("eventId.lessThan=" + UPDATED_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByEventIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where eventId is greater than DEFAULT_EVENT_ID
        defaultEventActivityShouldNotBeFound("eventId.greaterThan=" + DEFAULT_EVENT_ID);

        // Get all the eventActivityList where eventId is greater than SMALLER_EVENT_ID
        defaultEventActivityShouldBeFound("eventId.greaterThan=" + SMALLER_EVENT_ID);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where startDate equals to DEFAULT_START_DATE
        defaultEventActivityShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the eventActivityList where startDate equals to UPDATED_START_DATE
        defaultEventActivityShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByStartDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where startDate not equals to DEFAULT_START_DATE
        defaultEventActivityShouldNotBeFound("startDate.notEquals=" + DEFAULT_START_DATE);

        // Get all the eventActivityList where startDate not equals to UPDATED_START_DATE
        defaultEventActivityShouldBeFound("startDate.notEquals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultEventActivityShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the eventActivityList where startDate equals to UPDATED_START_DATE
        defaultEventActivityShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where startDate is not null
        defaultEventActivityShouldBeFound("startDate.specified=true");

        // Get all the eventActivityList where startDate is null
        defaultEventActivityShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllEventActivitiesByDurationInDayIsEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where durationInDay equals to DEFAULT_DURATION_IN_DAY
        defaultEventActivityShouldBeFound("durationInDay.equals=" + DEFAULT_DURATION_IN_DAY);

        // Get all the eventActivityList where durationInDay equals to UPDATED_DURATION_IN_DAY
        defaultEventActivityShouldNotBeFound("durationInDay.equals=" + UPDATED_DURATION_IN_DAY);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByDurationInDayIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where durationInDay not equals to DEFAULT_DURATION_IN_DAY
        defaultEventActivityShouldNotBeFound("durationInDay.notEquals=" + DEFAULT_DURATION_IN_DAY);

        // Get all the eventActivityList where durationInDay not equals to UPDATED_DURATION_IN_DAY
        defaultEventActivityShouldBeFound("durationInDay.notEquals=" + UPDATED_DURATION_IN_DAY);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByDurationInDayIsInShouldWork() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where durationInDay in DEFAULT_DURATION_IN_DAY or UPDATED_DURATION_IN_DAY
        defaultEventActivityShouldBeFound("durationInDay.in=" + DEFAULT_DURATION_IN_DAY + "," + UPDATED_DURATION_IN_DAY);

        // Get all the eventActivityList where durationInDay equals to UPDATED_DURATION_IN_DAY
        defaultEventActivityShouldNotBeFound("durationInDay.in=" + UPDATED_DURATION_IN_DAY);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByDurationInDayIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where durationInDay is not null
        defaultEventActivityShouldBeFound("durationInDay.specified=true");

        // Get all the eventActivityList where durationInDay is null
        defaultEventActivityShouldNotBeFound("durationInDay.specified=false");
    }

    @Test
    @Transactional
    void getAllEventActivitiesByDurationInDayIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where durationInDay is greater than or equal to DEFAULT_DURATION_IN_DAY
        defaultEventActivityShouldBeFound("durationInDay.greaterThanOrEqual=" + DEFAULT_DURATION_IN_DAY);

        // Get all the eventActivityList where durationInDay is greater than or equal to UPDATED_DURATION_IN_DAY
        defaultEventActivityShouldNotBeFound("durationInDay.greaterThanOrEqual=" + UPDATED_DURATION_IN_DAY);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByDurationInDayIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where durationInDay is less than or equal to DEFAULT_DURATION_IN_DAY
        defaultEventActivityShouldBeFound("durationInDay.lessThanOrEqual=" + DEFAULT_DURATION_IN_DAY);

        // Get all the eventActivityList where durationInDay is less than or equal to SMALLER_DURATION_IN_DAY
        defaultEventActivityShouldNotBeFound("durationInDay.lessThanOrEqual=" + SMALLER_DURATION_IN_DAY);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByDurationInDayIsLessThanSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where durationInDay is less than DEFAULT_DURATION_IN_DAY
        defaultEventActivityShouldNotBeFound("durationInDay.lessThan=" + DEFAULT_DURATION_IN_DAY);

        // Get all the eventActivityList where durationInDay is less than UPDATED_DURATION_IN_DAY
        defaultEventActivityShouldBeFound("durationInDay.lessThan=" + UPDATED_DURATION_IN_DAY);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByDurationInDayIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where durationInDay is greater than DEFAULT_DURATION_IN_DAY
        defaultEventActivityShouldNotBeFound("durationInDay.greaterThan=" + DEFAULT_DURATION_IN_DAY);

        // Get all the eventActivityList where durationInDay is greater than SMALLER_DURATION_IN_DAY
        defaultEventActivityShouldBeFound("durationInDay.greaterThan=" + SMALLER_DURATION_IN_DAY);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where name equals to DEFAULT_NAME
        defaultEventActivityShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the eventActivityList where name equals to UPDATED_NAME
        defaultEventActivityShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where name not equals to DEFAULT_NAME
        defaultEventActivityShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the eventActivityList where name not equals to UPDATED_NAME
        defaultEventActivityShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where name in DEFAULT_NAME or UPDATED_NAME
        defaultEventActivityShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the eventActivityList where name equals to UPDATED_NAME
        defaultEventActivityShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where name is not null
        defaultEventActivityShouldBeFound("name.specified=true");

        // Get all the eventActivityList where name is null
        defaultEventActivityShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllEventActivitiesByNameContainsSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where name contains DEFAULT_NAME
        defaultEventActivityShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the eventActivityList where name contains UPDATED_NAME
        defaultEventActivityShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEventActivitiesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList where name does not contain DEFAULT_NAME
        defaultEventActivityShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the eventActivityList where name does not contain UPDATED_NAME
        defaultEventActivityShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventActivityShouldBeFound(String filter) throws Exception {
        restEventActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventActivity.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].durationInDay").value(hasItem(sameNumber(DEFAULT_DURATION_IN_DAY))))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));

        // Check, that the count call also returns 1
        restEventActivityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventActivityShouldNotBeFound(String filter) throws Exception {
        restEventActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventActivityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("0"));
    }


    @Test
    @WithCurrentCCAdministrator
    public void getEventActivity() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        // Get the eventActivity
        restEventActivityMockMvc.perform(get("/api/event-activities/{id}/event/{eventId}", eventActivity.getId(), savedEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(savedEventActivity.getId().intValue()))
            .andExpect(jsonPath("$.eventId").value(savedEvent.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.durationInDay").value(DEFAULT_DURATION_IN_DAY.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @WithCurrentCCAdministrator
    public void getNonExistingEventActivity() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();

        // Get the eventActivity
        restEventActivityMockMvc.perform(get("/api/event-activities/{id}/eventId/{eventId}", Long.MAX_VALUE, savedEvent.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateEventActivity() throws Exception {
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());
        eventActivityDTO.setEventId(savedEventActivity.getEventId());

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isOk());

        // Validate the EventActivity in the database
        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeUpdate);
        EventActivity testEventActivity = eventActivityList.get(eventActivityList.size() - 1);
        assertThat(testEventActivity.getId()).isEqualTo(savedEventActivity.getId());
        assertThat(testEventActivity.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testEventActivity.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testEventActivity.getDurationInDay()).isEqualTo(UPDATED_DURATION_IN_DAY.setScale(2));
        assertThat(testEventActivity.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEventActivity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateEventActivity_WithEventActivityNotExist_ShouldThrow400() throws Exception {
        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(null);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventActivity in the database
        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateEventActivity_WithEventEnded_ShouldThrow400() throws Exception {
        event.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS));
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());
        eventActivityDTO.setEventId(savedEventActivity.getEventId());

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateEventActivity_WithEventClosed_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CLOSED);
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());
        eventActivityDTO.setEventId(savedEventActivity.getEventId());

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateEventActivity_WithEventCancelled_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CANCELLED);
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());
        eventActivityDTO.setEventId(savedEventActivity.getEventId());

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateEventActivity_WithEventActivityStartDateEarlierThanToday_ShouldThrow400() throws Exception {
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());
        eventActivityDTO.setEventId(savedEventActivity.getEventId());
        eventActivityDTO.setStartDate(Instant.now().minus(1, ChronoUnit.SECONDS));

        assertThat(eventActivityDTO.getStartDate()).isBefore(Instant.now());

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateEventActivity_WithEventActivityStartDateLaterThanEventEndDate_ShouldThrow400() throws Exception {
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());
        eventActivityDTO.setEventId(savedEventActivity.getEventId());
        eventActivityDTO.setStartDate(savedEvent.getEndDate().plus(1, ChronoUnit.SECONDS));

        assertThat(eventActivityDTO.getStartDate()).isAfter(savedEvent.getEndDate());

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void deleteEventActivity() throws Exception {
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeDelete = eventActivityRepository.findAll().size();

        restEventActivityMockMvc.perform(delete("/api/event-activities/{id}", eventActivity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @WithCurrentCCAdministrator
    public void deleteEventActivity_WithEventActivityNotExist_ShouldThrow400() throws Exception {

        int databaseSizeBeforeDelete = eventActivityRepository.findAll().size();

        restEventActivityMockMvc.perform(delete("/api/event-activities/{id}", Long.MAX_VALUE)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @WithCurrentCCAdministrator
    public void deleteEventActivity_WithEventIsCancelled_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CANCELLED);
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeDelete = eventActivityRepository.findAll().size();

        assertThat(savedEvent.getStatus()).isEqualByComparingTo(EventStatus.CANCELLED);

        restEventActivityMockMvc.perform(delete("/api/event-activities/{id}", savedEventActivity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @WithCurrentCCAdministrator
    public void deleteEventActivity_WithEventIsClosed_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CLOSED);
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeDelete = eventActivityRepository.findAll().size();

        assertThat(savedEvent.getStatus()).isEqualByComparingTo(EventStatus.CLOSED);

        restEventActivityMockMvc.perform(delete("/api/event-activities/{id}", savedEventActivity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @WithCurrentCCAdministrator
    public void deleteEventActivity_WithEventIsEnded_ShouldThrow400() throws Exception {
        event.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS));
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeDelete = eventActivityRepository.findAll().size();

        assertThat(savedEvent.getEndDate()).isBefore(Instant.now());

        restEventActivityMockMvc.perform(delete("/api/event-activities/{id}", savedEventActivity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventActivity.class);
        EventActivity eventActivity1 = new EventActivity();
        eventActivity1.setId(1L);
        EventActivity eventActivity2 = new EventActivity();
        eventActivity2.setId(eventActivity1.getId());
        assertThat(eventActivity1).isEqualTo(eventActivity2);
        eventActivity2.setId(2L);
        assertThat(eventActivity1).isNotEqualTo(eventActivity2);
        eventActivity1.setId(null);
        assertThat(eventActivity1).isNotEqualTo(eventActivity2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventActivityDTO.class);
        EventActivityDTO eventActivityDTO1 = new EventActivityDTO();
        eventActivityDTO1.setId(1L);
        EventActivityDTO eventActivityDTO2 = new EventActivityDTO();
        assertThat(eventActivityDTO1).isNotEqualTo(eventActivityDTO2);
        eventActivityDTO2.setId(eventActivityDTO1.getId());
        assertThat(eventActivityDTO1).isEqualTo(eventActivityDTO2);
        eventActivityDTO2.setId(2L);
        assertThat(eventActivityDTO1).isNotEqualTo(eventActivityDTO2);
        eventActivityDTO1.setId(null);
        assertThat(eventActivityDTO1).isNotEqualTo(eventActivityDTO2);
    }

    @Test
    public void testEntityFromId() {
        assertThat(eventActivityMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(eventActivityMapper.fromId(null)).isNull();
    }

    private Event initEventDB() {
        return eventRepository.saveAndFlush(event);
    }

    private EventActivity initEventActivityDB(Event event) {
        eventActivity.setEventId(event.getId());
        return eventActivityRepository.saveAndFlush(eventActivity);
    }
}
