package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventChecklist;
import com.thirdcc.webapp.domain.enumeration.EventChecklistStatus;
import com.thirdcc.webapp.domain.enumeration.EventChecklistType;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.repository.EventChecklistRepository;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.EventChecklistService;
import com.thirdcc.webapp.service.dto.EventChecklistDTO;
import com.thirdcc.webapp.service.mapper.EventChecklistMapper;

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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link ChecklistResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = "user")
public class EventChecklistResourceIT {

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final EventChecklistStatus DEFAULT_STATUS = EventChecklistStatus.OPEN;
    private static final EventChecklistStatus UPDATED_STATUS = EventChecklistStatus.IN_PROGRESS;

    private static final EventChecklistType DEFAULT_TYPE = EventChecklistType.PREPARATION;
    private static final EventChecklistType UPDATED_TYPE = EventChecklistType.PURCHASE;

    private static final String DEFAULT_EVENT_REMARKS = "DEFAULT_REMARKS";
    private static final String DEFAULT_EVENT_VENUE = "DEFAULT_VENUE";
    private static final Instant DEFAULT_EVENT_START_DATE = Instant.now().plus(1, ChronoUnit.DAYS);
    private static final Instant DEFAULT_EVENT_END_DATE = Instant.now().plus(2, ChronoUnit.DAYS);
    private static final EventStatus DEFAULT_EVENT_STATUS = EventStatus.OPEN;
    private static final BigDecimal DEFAULT_EVENT_FEE = BigDecimal.valueOf(10.0);

    @Autowired
    private EventChecklistRepository eventChecklistRepository;

    @Autowired
    private EventChecklistMapper eventChecklistMapper;

    @Autowired
    private EventChecklistService eventChecklistService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChecklistMockMvc;

    private EventChecklist eventChecklist;
    private Event event;

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
    public static EventChecklist createEventChecklistEntity(EntityManager em) {
        return new EventChecklist()
            .eventId(DEFAULT_EVENT_ID)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .type(DEFAULT_TYPE);
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventChecklist createUpdatedEventChecklistEntity(EntityManager em) {
        return new EventChecklist()
            .eventId(UPDATED_EVENT_ID)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE);
    }

    public static Event createEventEntity() {
        return new Event()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .remarks(DEFAULT_EVENT_REMARKS)
            .startDate(DEFAULT_EVENT_START_DATE)
            .endDate(DEFAULT_EVENT_END_DATE)
            .status(DEFAULT_EVENT_STATUS)
            .venue(DEFAULT_EVENT_VENUE)
            .fee(DEFAULT_EVENT_FEE);
    }

    @BeforeEach
    public void initTest() {
        event = createEventEntity();
        eventChecklist = createEventChecklistEntity(em);
    }

    @AfterEach
    public void cleanUp() {
        eventRepository.deleteAll();
        eventChecklistRepository.deleteAll();
    }

    private void initEventDB() {
        eventRepository.saveAndFlush(event);
    }

    private void initEventChecklistDB() {
        eventChecklistRepository.saveAndFlush(eventChecklist);
    }

    @Test
    public void createChecklist() throws Exception {
        initEventDB();

        int databaseSizeBeforeCreate = eventChecklistRepository.findAll().size();

        // Create the Checklist
        eventChecklist.setEventId(event.getId());
        EventChecklistDTO eventChecklistDTO = eventChecklistMapper.toDto(eventChecklist);
        restChecklistMockMvc.perform(post("/api/event-checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventChecklistDTO)))
            .andExpect(status().isCreated());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeCreate + 1);
        EventChecklist testChecklist = eventChecklistList.get(eventChecklistList.size() - 1);
        assertThat(testChecklist.getEventId()).isEqualTo(event.getId());
        assertThat(testChecklist.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testChecklist.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testChecklist.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testChecklist.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    public void createChecklist_WithExistingId_ShouldThrow400() throws Exception {
        initEventDB();
        initEventChecklistDB();

        int databaseSizeBeforeCreate = eventChecklistRepository.findAll().size();

        // Create the Checklist with an existing ID
        EventChecklist testEventChecklist = createEventChecklistEntity(em);
        testEventChecklist.setId(eventChecklist.getId());
        testEventChecklist.setEventId(event.getId());
        EventChecklistDTO eventChecklistDTO = eventChecklistMapper.toDto(testEventChecklist);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChecklistMockMvc.perform(post("/api/event-checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventChecklistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void createChecklist_WithEventStarted_ShouldThrow400() throws Exception {
        event.setStartDate(Instant.now().minus(1, ChronoUnit.DAYS));
        initEventDB();

        int databaseSizeBeforeCreate = eventChecklistRepository.findAll().size();

        eventChecklist.setEventId(event.getId());
        EventChecklistDTO eventChecklistDTO = eventChecklistMapper.toDto(eventChecklist);

        restChecklistMockMvc.perform(post("/api/event-checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventChecklistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void createChecklist_WithEventCancelled_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CANCELLED);
        initEventDB();

        int databaseSizeBeforeCreate = eventChecklistRepository.findAll().size();

        eventChecklist.setEventId(event.getId());
        EventChecklistDTO eventChecklistDTO = eventChecklistMapper.toDto(eventChecklist);

        restChecklistMockMvc.perform(post("/api/event-checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventChecklistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void getAllChecklists() throws Exception {
        // Initialize the database
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        // Get all the eventChecklistList
        restChecklistMockMvc.perform(get("/api/event-checklists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventChecklist.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    public void getAllEventChecklistsByEventId() throws Exception {
        // Initialize the database
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        // Get all the eventChecklistList
        restChecklistMockMvc.perform(get("/api/event-checklists/event/{eventId}?sort=id,desc", event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventChecklist.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(eventChecklist.getEventId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    public void getAllEventChecklistsByEventId_EventIsCancelled_ShouldThrow400() throws Exception {
        // Initialize the database
        event.setStatus(EventStatus.CANCELLED);
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        // Get all the eventChecklistList
        restChecklistMockMvc.perform(get("/api/event-checklists/event/{eventId}?sort=id,desc", eventChecklist.getEventId()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllEventChecklistsByEventId_NoEventChecklistsWithEventId_ShouldReturnEmptyList() throws Exception {
        // Initialize the database
        initEventDB();
        eventChecklist.setEventId(Long.MAX_VALUE);
        initEventChecklistDB();

        // Get all the eventChecklistList
        restChecklistMockMvc.perform(get("/api/event-checklists/event/{eventId}?sort=id,desc", event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getChecklist() throws Exception {
        // Initialize the database
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        // Get the eventChecklist
        restChecklistMockMvc.perform(get("/api/event-checklists/{id}", eventChecklist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(eventChecklist.getId().intValue()))
            .andExpect(jsonPath("$.eventId").value(event.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.eventName").value(event.getName()));
    }

    @Test
    public void getNonExistingChecklist() throws Exception {
        // Get the eventChecklist
        restChecklistMockMvc.perform(get("/api/event-checklists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateChecklist() throws Exception {
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        int databaseSizeBeforeUpdate = eventChecklistRepository.findAll().size();

        // Update the eventChecklist
        EventChecklist updatedChecklist = eventChecklistRepository.findById(eventChecklist.getId()).get();
        // Disconnect from session so that the updates on updatedChecklist are not directly saved in db
        em.detach(updatedChecklist);
        updatedChecklist
            .eventId(UPDATED_EVENT_ID)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE);
        EventChecklistDTO eventChecklistDTO = eventChecklistMapper.toDto(updatedChecklist);
        restChecklistMockMvc.perform(put("/api/event-checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventChecklistDTO)))
            .andExpect(status().isOk());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeUpdate);
        EventChecklist testChecklist = eventChecklistList.get(eventChecklistList.size() - 1);
        assertThat(testChecklist.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testChecklist.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testChecklist.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testChecklist.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testChecklist.getEventId()).isEqualTo(event.getId());
    }

    @Test
    public void updateChecklist_NonExisting_ShouldThrow400() throws Exception {
        initEventDB();

        int databaseSizeBeforeUpdate = eventChecklistRepository.findAll().size();

        // Create the Checklist
        eventChecklist.setEventId(event.getId());
        EventChecklistDTO eventChecklistDTO = eventChecklistMapper.toDto(eventChecklist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChecklistMockMvc.perform(put("/api/event-checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventChecklistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void updateChecklist_WithEventStarted_ShouldThrow400() throws Exception {
        event.setStartDate(Instant.now().minus(1, ChronoUnit.DAYS));
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        int databaseSizeBeforeUpdate = eventChecklistRepository.findAll().size();

        // Update the eventChecklist
        eventChecklist.setEventId(event.getId());
        EventChecklist updatedChecklist = eventChecklistRepository.findById(eventChecklist.getId()).get();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChecklistMockMvc.perform(put("/api/event-checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedChecklist)))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void updateChecklist_WithEventCancelled_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CANCELLED);
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        int databaseSizeBeforeUpdate = eventChecklistRepository.findAll().size();

        // Update the eventChecklist
        eventChecklist.setEventId(event.getId());
        EventChecklist updatedChecklist = eventChecklistRepository.findById(eventChecklist.getId()).get();

        em.detach(updatedChecklist);
        updatedChecklist
            .eventId(event.getId())
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE);
        EventChecklistDTO eventChecklistDTO = eventChecklistMapper.toDto(updatedChecklist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChecklistMockMvc.perform(put("/api/event-checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventChecklistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void updateChecklist_WithEventChecklistFinished_ShouldThrow400() throws Exception {
        initEventDB();
        eventChecklist.setEventId(event.getId());
        eventChecklist.setStatus(EventChecklistStatus.FINISHED);
        initEventChecklistDB();

        int databaseSizeBeforeUpdate = eventChecklistRepository.findAll().size();

        // Update the eventChecklist
        eventChecklist.setEventId(event.getId());
        EventChecklist updatedChecklist = eventChecklistRepository.findById(eventChecklist.getId()).get();

        em.detach(updatedChecklist);
        updatedChecklist
            .eventId(event.getId())
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE);
        EventChecklistDTO eventChecklistDTO = eventChecklistMapper.toDto(updatedChecklist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChecklistMockMvc.perform(put("/api/event-checklists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventChecklistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteChecklist() throws Exception {
        // Initialize the database
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        int databaseSizeBeforeDelete = eventChecklistRepository.findAll().size();

        // Delete the eventChecklist
        restChecklistMockMvc.perform(delete("/api/event-checklists/{id}", eventChecklist.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void deleteChecklist_EventChecklistNotExists_ShouldThrow400() throws Exception {
        // Initialize the database
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        int databaseSizeBeforeDelete = eventChecklistRepository.findAll().size();

        // Delete the eventChecklist
        restChecklistMockMvc.perform(delete("/api/event-checklists/{id}", Long.MAX_VALUE)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the database contains one less item
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    public void deleteChecklist_EventIsCancelled_ShouldThrow400() throws Exception {
        // Initialize the database
        event.setStatus(EventStatus.CANCELLED);
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        int databaseSizeBeforeDelete = eventChecklistRepository.findAll().size();

        // Delete the eventChecklist
        restChecklistMockMvc.perform(delete("/api/event-checklists/{id}", eventChecklist.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the database contains one less item
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    public void deleteChecklist_EventIsStarted_ShouldThrow400() throws Exception {
        // Initialize the database
        event.setStartDate(Instant.now().minus(1, ChronoUnit.DAYS));
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        int databaseSizeBeforeDelete = eventChecklistRepository.findAll().size();

        // Delete the eventChecklist
        restChecklistMockMvc.perform(delete("/api/event-checklists/{id}", eventChecklist.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the database contains one less item
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    public void deleteChecklist_EventChecklistStatusNotOpen_ShouldThrow400() throws Exception {
        // Initialize the database
        initEventDB();
        eventChecklist.setEventId(event.getId());
        eventChecklist.setStatus(EventChecklistStatus.IN_PROGRESS);
        initEventChecklistDB();

        int databaseSizeBeforeDelete = eventChecklistRepository.findAll().size();

        // Delete the eventChecklist
        restChecklistMockMvc.perform(delete("/api/event-checklists/{id}", eventChecklist.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the database contains one less item
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    public void updateEventChecklistStatus() throws Exception {
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        int databaseSizeBeforeUpdate = eventChecklistRepository.findAll().size();

        restChecklistMockMvc.perform(put("/api/event-checklists/{id}/status/{eventChecklistStatus}", eventChecklist.getId(), UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeUpdate);
        EventChecklist testChecklist = eventChecklistList.get(eventChecklistList.size() - 1);
        assertThat(testChecklist.getName()).isEqualTo(eventChecklist.getName());
        assertThat(testChecklist.getDescription()).isEqualTo(eventChecklist.getDescription());
        assertThat(testChecklist.getType()).isEqualTo(eventChecklist.getType());
        assertThat(testChecklist.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testChecklist.getEventId()).isEqualTo(event.getId());
    }

    @Test
    public void updateEventChecklistStatus_EventChecklistIsNotExists_ShouldThrow400() throws Exception {
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        int databaseSizeBeforeUpdate = eventChecklistRepository.findAll().size();

        restChecklistMockMvc.perform(put("/api/event-checklists/{id}/status/{eventChecklistStatus}", Long.MAX_VALUE, UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void updateEventChecklistStatus_EventIsCancelled_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CANCELLED);
        initEventDB();
        eventChecklist.setEventId(event.getId());
        initEventChecklistDB();

        int databaseSizeBeforeUpdate = eventChecklistRepository.findAll().size();

        restChecklistMockMvc.perform(put("/api/event-checklists/{id}/status/{eventChecklistStatus}", eventChecklist.getId(), UPDATED_STATUS)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the Checklist in the database
        List<EventChecklist> eventChecklistList = eventChecklistRepository.findAll();
        assertThat(eventChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventChecklist.class);
        EventChecklist eventChecklist1 = new EventChecklist();
        eventChecklist1.setId(1L);
        EventChecklist eventChecklist2 = new EventChecklist();
        eventChecklist2.setId(eventChecklist1.getId());
        assertThat(eventChecklist1).isEqualTo(eventChecklist2);
        eventChecklist2.setId(2L);
        assertThat(eventChecklist1).isNotEqualTo(eventChecklist2);
        eventChecklist1.setId(null);
        assertThat(eventChecklist1).isNotEqualTo(eventChecklist2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventChecklistDTO.class);
        EventChecklistDTO eventChecklistDTO1 = new EventChecklistDTO();
        eventChecklistDTO1.setId(1L);
        EventChecklistDTO eventChecklistDTO2 = new EventChecklistDTO();
        assertThat(eventChecklistDTO1).isNotEqualTo(eventChecklistDTO2);
        eventChecklistDTO2.setId(eventChecklistDTO1.getId());
        assertThat(eventChecklistDTO1).isEqualTo(eventChecklistDTO2);
        eventChecklistDTO2.setId(2L);
        assertThat(eventChecklistDTO1).isNotEqualTo(eventChecklistDTO2);
        eventChecklistDTO1.setId(null);
        assertThat(eventChecklistDTO1).isNotEqualTo(eventChecklistDTO2);
    }

    @Test
    public void testEntityFromId() {
        assertThat(eventChecklistMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(eventChecklistMapper.fromId(null)).isNull();
    }
}
