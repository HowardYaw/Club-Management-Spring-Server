package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.EventRegistrationClosingCriteria;
import com.thirdcc.webapp.repository.EventRegistrationClosingCriteriaRepository;
import com.thirdcc.webapp.service.EventRegistrationClosingCriteriaService;
import com.thirdcc.webapp.service.dto.EventRegistrationClosingCriteriaDTO;
import com.thirdcc.webapp.service.mapper.EventRegistrationClosingCriteriaMapper;
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
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.thirdcc.webapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link EventRegistrationClosingCriteriaResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
public class EventRegistrationClosingCriteriaResourceIT {

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final Integer DEFAULT_MAX_ATTENDEES = 1;
    private static final Integer UPDATED_MAX_ATTENDEES = 2;

    private static final Instant DEFAULT_CLOSING_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CLOSING_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_FORCE_CLOSE = false;
    private static final Boolean UPDATED_FORCE_CLOSE = true;

    @Autowired
    private EventRegistrationClosingCriteriaRepository eventRegistrationClosingCriteriaRepository;

    @Autowired
    private EventRegistrationClosingCriteriaMapper eventRegistrationClosingCriteriaMapper;

    @Autowired
    private EventRegistrationClosingCriteriaService eventRegistrationClosingCriteriaService;

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

    private MockMvc restEventRegistrationClosingCriteriaMockMvc;

    private EventRegistrationClosingCriteria eventRegistrationClosingCriteria;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EventRegistrationClosingCriteriaResource eventRegistrationClosingCriteriaResource = new EventRegistrationClosingCriteriaResource(eventRegistrationClosingCriteriaService);
        this.restEventRegistrationClosingCriteriaMockMvc = MockMvcBuilders.standaloneSetup(eventRegistrationClosingCriteriaResource)
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
    public static EventRegistrationClosingCriteria createEntity(EntityManager em) {
        EventRegistrationClosingCriteria eventRegistrationClosingCriteria = new EventRegistrationClosingCriteria()
            .eventId(DEFAULT_EVENT_ID)
            .maxAttendees(DEFAULT_MAX_ATTENDEES)
            .closingDate(DEFAULT_CLOSING_DATE)
            .forceClose(DEFAULT_FORCE_CLOSE);
        return eventRegistrationClosingCriteria;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventRegistrationClosingCriteria createUpdatedEntity(EntityManager em) {
        EventRegistrationClosingCriteria eventRegistrationClosingCriteria = new EventRegistrationClosingCriteria()
            .eventId(UPDATED_EVENT_ID)
            .maxAttendees(UPDATED_MAX_ATTENDEES)
            .closingDate(UPDATED_CLOSING_DATE)
            .forceClose(UPDATED_FORCE_CLOSE);
        return eventRegistrationClosingCriteria;
    }

    @BeforeEach
    public void initTest() {
        eventRegistrationClosingCriteria = createEntity(em);
    }

    @Test
    @Transactional
    public void createEventRegistrationClosingCriteria() throws Exception {
        int databaseSizeBeforeCreate = eventRegistrationClosingCriteriaRepository.findAll().size();

        // Create the EventRegistrationClosingCriteria
        EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO = eventRegistrationClosingCriteriaMapper.toDto(eventRegistrationClosingCriteria);
        restEventRegistrationClosingCriteriaMockMvc.perform(post("/api/event-registration-closing-criteria")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventRegistrationClosingCriteriaDTO)))
            .andExpect(status().isCreated());

        // Validate the EventRegistrationClosingCriteria in the database
        List<EventRegistrationClosingCriteria> eventRegistrationClosingCriteriaList = eventRegistrationClosingCriteriaRepository.findAll();
        assertThat(eventRegistrationClosingCriteriaList).hasSize(databaseSizeBeforeCreate + 1);
        EventRegistrationClosingCriteria testEventRegistrationClosingCriteria = eventRegistrationClosingCriteriaList.get(eventRegistrationClosingCriteriaList.size() - 1);
        assertThat(testEventRegistrationClosingCriteria.getEventId()).isEqualTo(DEFAULT_EVENT_ID);
        assertThat(testEventRegistrationClosingCriteria.getMaxAttendees()).isEqualTo(DEFAULT_MAX_ATTENDEES);
        assertThat(testEventRegistrationClosingCriteria.getClosingDate()).isEqualTo(DEFAULT_CLOSING_DATE);
        assertThat(testEventRegistrationClosingCriteria.isForceClose()).isEqualTo(DEFAULT_FORCE_CLOSE);
    }

    @Test
    @Transactional
    public void createEventRegistrationClosingCriteriaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = eventRegistrationClosingCriteriaRepository.findAll().size();

        // Create the EventRegistrationClosingCriteria with an existing ID
        eventRegistrationClosingCriteria.setId(1L);
        EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO = eventRegistrationClosingCriteriaMapper.toDto(eventRegistrationClosingCriteria);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventRegistrationClosingCriteriaMockMvc.perform(post("/api/event-registration-closing-criteria")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventRegistrationClosingCriteriaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventRegistrationClosingCriteria in the database
        List<EventRegistrationClosingCriteria> eventRegistrationClosingCriteriaList = eventRegistrationClosingCriteriaRepository.findAll();
        assertThat(eventRegistrationClosingCriteriaList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllEventRegistrationClosingCriteria() throws Exception {
        // Initialize the database
        eventRegistrationClosingCriteriaRepository.saveAndFlush(eventRegistrationClosingCriteria);

        // Get all the eventRegistrationClosingCriteriaList
        restEventRegistrationClosingCriteriaMockMvc.perform(get("/api/event-registration-closing-criteria?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventRegistrationClosingCriteria.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].maxAttendees").value(hasItem(DEFAULT_MAX_ATTENDEES)))
            .andExpect(jsonPath("$.[*].closingDate").value(hasItem(DEFAULT_CLOSING_DATE.toString())))
            .andExpect(jsonPath("$.[*].forceClose").value(hasItem(DEFAULT_FORCE_CLOSE.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getEventRegistrationClosingCriteria() throws Exception {
        // Initialize the database
        eventRegistrationClosingCriteriaRepository.saveAndFlush(eventRegistrationClosingCriteria);

        // Get the eventRegistrationClosingCriteria
        restEventRegistrationClosingCriteriaMockMvc.perform(get("/api/event-registration-closing-criteria/{id}", eventRegistrationClosingCriteria.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(eventRegistrationClosingCriteria.getId().intValue()))
            .andExpect(jsonPath("$.eventId").value(DEFAULT_EVENT_ID.intValue()))
            .andExpect(jsonPath("$.maxAttendees").value(DEFAULT_MAX_ATTENDEES))
            .andExpect(jsonPath("$.closingDate").value(DEFAULT_CLOSING_DATE.toString()))
            .andExpect(jsonPath("$.forceClose").value(DEFAULT_FORCE_CLOSE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingEventRegistrationClosingCriteria() throws Exception {
        // Get the eventRegistrationClosingCriteria
        restEventRegistrationClosingCriteriaMockMvc.perform(get("/api/event-registration-closing-criteria/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEventRegistrationClosingCriteria() throws Exception {
        // Initialize the database
        eventRegistrationClosingCriteriaRepository.saveAndFlush(eventRegistrationClosingCriteria);

        int databaseSizeBeforeUpdate = eventRegistrationClosingCriteriaRepository.findAll().size();

        // Update the eventRegistrationClosingCriteria
        EventRegistrationClosingCriteria updatedEventRegistrationClosingCriteria = eventRegistrationClosingCriteriaRepository.findById(eventRegistrationClosingCriteria.getId()).get();
        // Disconnect from session so that the updates on updatedEventRegistrationClosingCriteria are not directly saved in db
        em.detach(updatedEventRegistrationClosingCriteria);
        updatedEventRegistrationClosingCriteria
            .eventId(UPDATED_EVENT_ID)
            .maxAttendees(UPDATED_MAX_ATTENDEES)
            .closingDate(UPDATED_CLOSING_DATE)
            .forceClose(UPDATED_FORCE_CLOSE);
        EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO = eventRegistrationClosingCriteriaMapper.toDto(updatedEventRegistrationClosingCriteria);

        restEventRegistrationClosingCriteriaMockMvc.perform(put("/api/event-registration-closing-criteria")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventRegistrationClosingCriteriaDTO)))
            .andExpect(status().isOk());

        // Validate the EventRegistrationClosingCriteria in the database
        List<EventRegistrationClosingCriteria> eventRegistrationClosingCriteriaList = eventRegistrationClosingCriteriaRepository.findAll();
        assertThat(eventRegistrationClosingCriteriaList).hasSize(databaseSizeBeforeUpdate);
        EventRegistrationClosingCriteria testEventRegistrationClosingCriteria = eventRegistrationClosingCriteriaList.get(eventRegistrationClosingCriteriaList.size() - 1);
        assertThat(testEventRegistrationClosingCriteria.getEventId()).isEqualTo(UPDATED_EVENT_ID);
        assertThat(testEventRegistrationClosingCriteria.getMaxAttendees()).isEqualTo(UPDATED_MAX_ATTENDEES);
        assertThat(testEventRegistrationClosingCriteria.getClosingDate()).isEqualTo(UPDATED_CLOSING_DATE);
        assertThat(testEventRegistrationClosingCriteria.isForceClose()).isEqualTo(UPDATED_FORCE_CLOSE);
    }

    @Test
    @Transactional
    public void updateNonExistingEventRegistrationClosingCriteria() throws Exception {
        int databaseSizeBeforeUpdate = eventRegistrationClosingCriteriaRepository.findAll().size();

        // Create the EventRegistrationClosingCriteria
        EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO = eventRegistrationClosingCriteriaMapper.toDto(eventRegistrationClosingCriteria);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventRegistrationClosingCriteriaMockMvc.perform(put("/api/event-registration-closing-criteria")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventRegistrationClosingCriteriaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventRegistrationClosingCriteria in the database
        List<EventRegistrationClosingCriteria> eventRegistrationClosingCriteriaList = eventRegistrationClosingCriteriaRepository.findAll();
        assertThat(eventRegistrationClosingCriteriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEventRegistrationClosingCriteria() throws Exception {
        // Initialize the database
        eventRegistrationClosingCriteriaRepository.saveAndFlush(eventRegistrationClosingCriteria);

        int databaseSizeBeforeDelete = eventRegistrationClosingCriteriaRepository.findAll().size();

        // Delete the eventRegistrationClosingCriteria
        restEventRegistrationClosingCriteriaMockMvc.perform(delete("/api/event-registration-closing-criteria/{id}", eventRegistrationClosingCriteria.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EventRegistrationClosingCriteria> eventRegistrationClosingCriteriaList = eventRegistrationClosingCriteriaRepository.findAll();
        assertThat(eventRegistrationClosingCriteriaList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventRegistrationClosingCriteria.class);
        EventRegistrationClosingCriteria eventRegistrationClosingCriteria1 = new EventRegistrationClosingCriteria();
        eventRegistrationClosingCriteria1.setId(1L);
        EventRegistrationClosingCriteria eventRegistrationClosingCriteria2 = new EventRegistrationClosingCriteria();
        eventRegistrationClosingCriteria2.setId(eventRegistrationClosingCriteria1.getId());
        assertThat(eventRegistrationClosingCriteria1).isEqualTo(eventRegistrationClosingCriteria2);
        eventRegistrationClosingCriteria2.setId(2L);
        assertThat(eventRegistrationClosingCriteria1).isNotEqualTo(eventRegistrationClosingCriteria2);
        eventRegistrationClosingCriteria1.setId(null);
        assertThat(eventRegistrationClosingCriteria1).isNotEqualTo(eventRegistrationClosingCriteria2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventRegistrationClosingCriteriaDTO.class);
        EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO1 = new EventRegistrationClosingCriteriaDTO();
        eventRegistrationClosingCriteriaDTO1.setId(1L);
        EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO2 = new EventRegistrationClosingCriteriaDTO();
        assertThat(eventRegistrationClosingCriteriaDTO1).isNotEqualTo(eventRegistrationClosingCriteriaDTO2);
        eventRegistrationClosingCriteriaDTO2.setId(eventRegistrationClosingCriteriaDTO1.getId());
        assertThat(eventRegistrationClosingCriteriaDTO1).isEqualTo(eventRegistrationClosingCriteriaDTO2);
        eventRegistrationClosingCriteriaDTO2.setId(2L);
        assertThat(eventRegistrationClosingCriteriaDTO1).isNotEqualTo(eventRegistrationClosingCriteriaDTO2);
        eventRegistrationClosingCriteriaDTO1.setId(null);
        assertThat(eventRegistrationClosingCriteriaDTO1).isNotEqualTo(eventRegistrationClosingCriteriaDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(eventRegistrationClosingCriteriaMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(eventRegistrationClosingCriteriaMapper.fromId(null)).isNull();
    }
}
