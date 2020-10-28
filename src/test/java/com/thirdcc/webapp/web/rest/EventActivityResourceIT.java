package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.EventActivity;
import com.thirdcc.webapp.repository.EventActivityRepository;
import com.thirdcc.webapp.service.EventActivityService;
import com.thirdcc.webapp.service.dto.EventActivityDTO;
import com.thirdcc.webapp.service.mapper.EventActivityMapper;
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

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_DURATION_IN_DAY = new BigDecimal(1);
    private static final BigDecimal UPDATED_DURATION_IN_DAY = new BigDecimal(2);

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

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

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventActivity createEntity(EntityManager em) {
        EventActivity eventActivity = new EventActivity()
            .eventId(DEFAULT_EVENT_ID)
            .startDate(DEFAULT_START_DATE)
            .durationInDay(DEFAULT_DURATION_IN_DAY)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
        return eventActivity;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventActivity createUpdatedEntity(EntityManager em) {
        EventActivity eventActivity = new EventActivity()
            .eventId(UPDATED_EVENT_ID)
            .startDate(UPDATED_START_DATE)
            .durationInDay(UPDATED_DURATION_IN_DAY)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);
        return eventActivity;
    }

    @BeforeEach
    public void initTest() {
        eventActivity = createEntity(em);
    }

    @Test
    @Transactional
    public void createEventActivity() throws Exception {
        int databaseSizeBeforeCreate = eventActivityRepository.findAll().size();

        // Create the EventActivity
        EventActivityDTO eventActivityDTO = eventActivityMapper.toDto(eventActivity);
        restEventActivityMockMvc.perform(post("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isCreated());

        // Validate the EventActivity in the database
        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeCreate + 1);
        EventActivity testEventActivity = eventActivityList.get(eventActivityList.size() - 1);
        assertThat(testEventActivity.getEventId()).isEqualTo(DEFAULT_EVENT_ID);
        assertThat(testEventActivity.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testEventActivity.getDurationInDay()).isEqualTo(DEFAULT_DURATION_IN_DAY);
        assertThat(testEventActivity.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEventActivity.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
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
    @Transactional
    public void getAllEventActivities() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get all the eventActivityList
        restEventActivityMockMvc.perform(get("/api/event-activities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventActivity.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].durationInDay").value(hasItem(DEFAULT_DURATION_IN_DAY.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
    
    @Test
    @Transactional
    public void getEventActivity() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        // Get the eventActivity
        restEventActivityMockMvc.perform(get("/api/event-activities/{id}", eventActivity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(eventActivity.getId().intValue()))
            .andExpect(jsonPath("$.eventId").value(DEFAULT_EVENT_ID.intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.durationInDay").value(DEFAULT_DURATION_IN_DAY.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEventActivity() throws Exception {
        // Get the eventActivity
        restEventActivityMockMvc.perform(get("/api/event-activities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEventActivity() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        // Update the eventActivity
        EventActivity updatedEventActivity = eventActivityRepository.findById(eventActivity.getId()).get();
        // Disconnect from session so that the updates on updatedEventActivity are not directly saved in db
        em.detach(updatedEventActivity);
        updatedEventActivity
            .eventId(UPDATED_EVENT_ID)
            .startDate(UPDATED_START_DATE)
            .durationInDay(UPDATED_DURATION_IN_DAY)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);
        EventActivityDTO eventActivityDTO = eventActivityMapper.toDto(updatedEventActivity);

        restEventActivityMockMvc.perform(put("/api/event-activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventActivityDTO)))
            .andExpect(status().isOk());

        // Validate the EventActivity in the database
        List<EventActivity> eventActivityList = eventActivityRepository.findAll();
        assertThat(eventActivityList).hasSize(databaseSizeBeforeUpdate);
        EventActivity testEventActivity = eventActivityList.get(eventActivityList.size() - 1);
        assertThat(testEventActivity.getEventId()).isEqualTo(UPDATED_EVENT_ID);
        assertThat(testEventActivity.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testEventActivity.getDurationInDay()).isEqualTo(UPDATED_DURATION_IN_DAY);
        assertThat(testEventActivity.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEventActivity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingEventActivity() throws Exception {
        int databaseSizeBeforeUpdate = eventActivityRepository.findAll().size();

        // Create the EventActivity
        EventActivityDTO eventActivityDTO = eventActivityMapper.toDto(eventActivity);

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
    @Transactional
    public void deleteEventActivity() throws Exception {
        // Initialize the database
        eventActivityRepository.saveAndFlush(eventActivity);

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
    @Transactional
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
    @Transactional
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
    @Transactional
    public void testEntityFromId() {
        assertThat(eventActivityMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(eventActivityMapper.fromId(null)).isNull();
    }
}
