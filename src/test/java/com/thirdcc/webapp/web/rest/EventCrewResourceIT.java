package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.repository.EventCrewRepository;
import com.thirdcc.webapp.service.EventCrewService;
import com.thirdcc.webapp.service.dto.EventCrewDTO;
import com.thirdcc.webapp.service.mapper.EventCrewMapper;

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
@WithMockUser(value = "user")
public class EventCrewResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final EventCrewRole DEFAULT_ROLE = EventCrewRole.HEAD;
    private static final EventCrewRole UPDATED_ROLE = EventCrewRole.HEAD;

    @Autowired
    private EventCrewRepository eventCrewRepository;

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
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventCrew createEntity(EntityManager em) {
        EventCrew eventCrew = new EventCrew()
            .userId(DEFAULT_USER_ID)
            .eventId(DEFAULT_EVENT_ID)
            .role(DEFAULT_ROLE);
        return eventCrew;
    }
    /**
     * Create an updated entity for this test.
     *
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

    @BeforeEach
    public void initTest() {
        eventCrew = createEntity(em);
    }

    @Test
    @Transactional
    public void createEventCrew() throws Exception {
        int databaseSizeBeforeCreate = eventCrewRepository.findAll().size();

        // Create the EventCrew
        EventCrewDTO eventCrewDTO = eventCrewMapper.toDto(eventCrew);
        restEventCrewMockMvc.perform(post("/api/event-crews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventCrewDTO)))
            .andExpect(status().isCreated());

        // Validate the EventCrew in the database
        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeCreate + 1);
        EventCrew testEventCrew = eventCrewList.get(eventCrewList.size() - 1);
        assertThat(testEventCrew.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testEventCrew.getEventId()).isEqualTo(DEFAULT_EVENT_ID);
        assertThat(testEventCrew.getRole()).isEqualTo(DEFAULT_ROLE);
    }

    @Test
    @Transactional
    public void createEventCrewWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = eventCrewRepository.findAll().size();

        // Create the EventCrew with an existing ID
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
    public void getAllEventCrews() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get all the eventCrewList
        restEventCrewMockMvc.perform(get("/api/event-crews?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventCrew.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }

    @Test
    @Transactional
    public void getEventCrew() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        // Get the eventCrew
        restEventCrewMockMvc.perform(get("/api/event-crews/{id}", eventCrew.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(eventCrew.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.eventId").value(DEFAULT_EVENT_ID.intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEventCrew() throws Exception {
        // Get the eventCrew
        restEventCrewMockMvc.perform(get("/api/event-crews/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEventCrew() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        int databaseSizeBeforeUpdate = eventCrewRepository.findAll().size();

        // Update the eventCrew
        EventCrew updatedEventCrew = eventCrewRepository.findById(eventCrew.getId()).get();
        // Disconnect from session so that the updates on updatedEventCrew are not directly saved in db
        em.detach(updatedEventCrew);
        updatedEventCrew
            .userId(UPDATED_USER_ID)
            .eventId(UPDATED_EVENT_ID)
            .role(UPDATED_ROLE);
        EventCrewDTO eventCrewDTO = eventCrewMapper.toDto(updatedEventCrew);

        restEventCrewMockMvc.perform(put("/api/event-crews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventCrewDTO)))
            .andExpect(status().isOk());

        // Validate the EventCrew in the database
        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeUpdate);
        EventCrew testEventCrew = eventCrewList.get(eventCrewList.size() - 1);
        assertThat(testEventCrew.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testEventCrew.getEventId()).isEqualTo(UPDATED_EVENT_ID);
        assertThat(testEventCrew.getRole()).isEqualTo(UPDATED_ROLE);
    }

    @Test
    @Transactional
    public void updateNonExistingEventCrew() throws Exception {
        int databaseSizeBeforeUpdate = eventCrewRepository.findAll().size();

        // Create the EventCrew
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
    public void deleteEventCrew() throws Exception {
        // Initialize the database
        eventCrewRepository.saveAndFlush(eventCrew);

        int databaseSizeBeforeDelete = eventCrewRepository.findAll().size();

        // Delete the eventCrew
        restEventCrewMockMvc.perform(delete("/api/event-crews/{id}", eventCrew.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EventCrew> eventCrewList = eventCrewRepository.findAll();
        assertThat(eventCrewList).hasSize(databaseSizeBeforeDelete - 1);
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
}
