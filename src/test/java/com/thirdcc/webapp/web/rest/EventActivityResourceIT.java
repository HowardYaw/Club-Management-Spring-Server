package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventActivity;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.repository.EventActivityRepository;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.EventActivityService;
import com.thirdcc.webapp.service.dto.EventActivityDTO;
import com.thirdcc.webapp.service.mapper.EventActivityMapper;
import com.thirdcc.webapp.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.util.Base64Utils;
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

/**
 * Integration tests for the {@Link EventActivityResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
public class EventActivityResourceIT {

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final Instant DEFAULT_START_DATE = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
    private static final Instant UPDATED_START_DATE = Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_DURATION_IN_DAY = new BigDecimal(1);
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
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restEventActivityMockMvc;

    private EventActivity eventActivity;

    private Event event;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EventActivityResource eventActivityResource = new EventActivityResource(eventActivityService);
        this.restEventActivityMockMvc = MockMvcBuilders.standaloneSetup(eventActivityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
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
    public void createEventActivity_WithStartDateEarlierThanToday_ShouldThrow400() throws Exception {
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
    public void createEventActivity_WithStartDateEarlierThanEventStartDate_ShouldThrow400() throws Exception {
        event.setStartDate(Instant.now().plus(1, ChronoUnit.DAYS));
        Event savedEvent = initEventDB();

        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createDefaultEventActivityDTO();
        eventActivityDTO.setEventId(savedEvent.getId());
        eventActivityDTO.setStartDate(savedEvent.getStartDate().minus(1, ChronoUnit.SECONDS));

        assertThat(savedEvent.getStartDate()).isAfter(Instant.now()); //for more accurate testing
        assertThat(eventActivityDTO.getStartDate()).isBefore(savedEvent.getStartDate());

        restEventActivityMockMvc.perform(post("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void createEventActivity_WithStartDateLaterThanEventEndDate_ShouldThrow400() throws Exception {
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
    public void getEventActivity() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        // Get the eventActivity
        restEventActivityMockMvc.perform(get("/api/event-activities/{id}", eventActivity.getId()))
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
    public void getNonExistingEventActivity() throws Exception {
        // Get the eventActivity
        restEventActivityMockMvc.perform(get("/api/event-activities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateEventActivity() throws Exception {
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());

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
    public void updateEventActivity_WithEventEnded_ShouldThrow400() throws Exception {
        event.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS));
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void updateEventActivity_WithEventClosed_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CLOSED);
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void updateEventActivity_WithEventCancelled_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CANCELLED);
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void updateEventActivity_WithEventEarlierThanToday_ShouldThrow400() throws Exception {
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());
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
    public void updateEventActivity_WithStartDateEarlierThanEventStartDate_ShouldThrow400() throws Exception {
        event.setStartDate(Instant.now().plus(1, ChronoUnit.DAYS));
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());
        eventActivityDTO.setStartDate(savedEvent.getStartDate().minus(1, ChronoUnit.SECONDS));

        assertThat(savedEvent.getStartDate()).isAfter(Instant.now()); //for more accurate testing
        assertThat(eventActivityDTO.getStartDate()).isBefore(savedEvent.getStartDate());

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isBadRequest());

        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void updateEventActivity_WithStartDateLaterThanEventEndDate_ShouldThrow400() throws Exception {
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        EventActivityDTO eventActivityDTO = createUpdateEventActivityDTO();
        eventActivityDTO.setId(savedEventActivity.getId());
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
    public void deleteEventActivity() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        EventActivity savedEventActivity = initEventActivityDB(savedEvent);

        int databaseSizeBeforeDelete = eventActivityRepository.findAll().size();

        // Delete the eventActivity
        restEventActivityMockMvc.perform(delete("/api/event-activities/{id}", eventActivity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeDelete - 1);
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
