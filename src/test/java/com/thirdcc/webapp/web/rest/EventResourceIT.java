package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.WithCurrentCCAdministrator;
import com.thirdcc.webapp.annotations.authorization.WithEventHead;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.*;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.EventCrewService;
import com.thirdcc.webapp.service.ImageStorageService;
import com.thirdcc.webapp.service.UserService;
import com.thirdcc.webapp.service.dto.EventCrewDTO;
import com.thirdcc.webapp.service.dto.EventDTO;
import com.thirdcc.webapp.service.dto.ImageStorageDTO;
import com.thirdcc.webapp.service.mapper.EventMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thirdcc.webapp.domain.enumeration.EventStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * Integration tests for the {@Link EventResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@InitYearSession
@WithMockUser
public class EventResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String DEFAULT_VENUE = "AAAAAAAAAA";
    private static final String UPDATED_VENUE = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_FEE = new BigDecimal(1);
    private static final BigDecimal UPDATED_FEE = new BigDecimal(2);

    private static final Boolean DEFAULT_REQUIRED_TRANSPORT = false;
    private static final Boolean UPDATED_REQUIRED_TRANSPORT = true;

    private static final EventStatus DEFAULT_STATUS = EventStatus.OPEN;
    private static final EventStatus UPDATED_STATUS = EventStatus.CLOSED;
    private static final EventStatus CANCELLED_STATUS = EventStatus.CANCELLED;

    private static final String DEFAULT_YEAR_SESSION_VALUE = "2021/2022";

    private static final Long DEFAULT_IMAGE_STORAGE_ID = 1L;

    private static final MockMultipartFile MOCK_MULTIPART_FILE = new MockMultipartFile("multipartFile", "Event Image".getBytes());
    private static final String DEFAULT_EVENT_IMAGE_LINK = "https://gcp/eventImage.jpg";

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private YearSessionRepository yearSessionRepository;

    @Autowired
    private EventCrewService eventCrewService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventCrewRepository eventCrewRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private MockMvc restEventMockMvc;

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
    public static Event createEntity() {
        return new Event()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .remarks(DEFAULT_REMARKS)
            .venue(DEFAULT_VENUE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .fee(DEFAULT_FEE)
            .requiredTransport(DEFAULT_REQUIRED_TRANSPORT)
            .status(DEFAULT_STATUS)
            .imageStorageId(DEFAULT_IMAGE_STORAGE_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createUpdatedEntity() {
        return new Event()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .remarks(UPDATED_REMARKS)
            .venue(UPDATED_VENUE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .fee(UPDATED_FEE)
            .requiredTransport(UPDATED_REQUIRED_TRANSPORT)
            .status(UPDATED_STATUS);
    }

    private YearSession initYearSessionDB() {
        return yearSessionRepository.saveAndFlush(new YearSession()
            .value(DEFAULT_YEAR_SESSION_VALUE));
    }

    @BeforeEach
    public void initTest() {
        event = createEntity();
    }

    @BeforeEach
    public void initStub() throws IOException {
        ImageStorageDTO mockImageSavingResult = new ImageStorageDTO();
        mockImageSavingResult.setId(DEFAULT_IMAGE_STORAGE_ID);
        mockImageSavingResult.setImageUrl(DEFAULT_EVENT_IMAGE_LINK);
        mockImageSavingResult.setFileType("image/jpeg");
        mockImageSavingResult.setFileName("Event Image");

        when(imageStorageService.save(Mockito.any(ImageStorageDTO.class), Mockito.any(MultipartFile.class)))
            .thenReturn(mockImageSavingResult);
    }

    @AfterEach
    public void cleanUp() {
        eventCrewRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void createEvent() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        restEventMockMvc.perform(post("/api/events")
            .param("name", DEFAULT_NAME)
            .param("description", DEFAULT_DESCRIPTION)
            .param("remarks", DEFAULT_REMARKS)
            .param("venue", DEFAULT_VENUE)
            .param("startDate", DEFAULT_START_DATE.toString())
            .param("endDate", DEFAULT_END_DATE.toString())
            .param("fee", DEFAULT_FEE.toString())
            .param("requiredTransport", DEFAULT_REQUIRED_TRANSPORT.toString())
            .param("status", DEFAULT_STATUS.toString())
        ).andExpect(status().isCreated());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEvent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEvent.getRemarks()).isEqualTo(DEFAULT_REMARKS);
        assertThat(testEvent.getVenue()).isEqualTo(DEFAULT_VENUE);
        assertThat(testEvent.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testEvent.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testEvent.getFee()).isEqualTo(DEFAULT_FEE);
        assertThat(testEvent.isRequiredTransport()).isEqualTo(DEFAULT_REQUIRED_TRANSPORT);
        assertThat(testEvent.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testEvent.getImageStorageId()).isNull();
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void createEventWithEventImage_ShouldSuccess() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        restEventMockMvc.perform(multipart("/api/events")
            .file(MOCK_MULTIPART_FILE)
            .param("name", DEFAULT_NAME)
            .param("description", DEFAULT_DESCRIPTION)
            .param("remarks", DEFAULT_REMARKS)
            .param("venue", DEFAULT_VENUE)
            .param("startDate", DEFAULT_START_DATE.toString())
            .param("endDate", DEFAULT_END_DATE.toString())
            .param("fee", DEFAULT_FEE.toString())
            .param("requiredTransport", DEFAULT_REQUIRED_TRANSPORT.toString())
            .param("status", DEFAULT_STATUS.toString())
        ).andExpect(status().isCreated());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEvent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEvent.getRemarks()).isEqualTo(DEFAULT_REMARKS);
        assertThat(testEvent.getVenue()).isEqualTo(DEFAULT_VENUE);
        assertThat(testEvent.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testEvent.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testEvent.getFee()).isEqualTo(DEFAULT_FEE);
        assertThat(testEvent.isRequiredTransport()).isEqualTo(DEFAULT_REQUIRED_TRANSPORT);
        assertThat(testEvent.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testEvent.getImageStorageId()).isEqualTo(DEFAULT_IMAGE_STORAGE_ID);
    }


    @Test
    @Transactional
    public void createEventWithUserRole_ShouldReturn403() throws Exception {

        restEventMockMvc.perform(post("/api/events")
            .param("name", DEFAULT_NAME)
            .param("description", DEFAULT_DESCRIPTION)
            .param("remarks", DEFAULT_REMARKS)
            .param("venue", DEFAULT_VENUE)
            .param("startDate", DEFAULT_START_DATE.toString())
            .param("endDate", DEFAULT_END_DATE.toString())
            .param("fee", DEFAULT_FEE.toString())
            .param("requiredTransport", DEFAULT_REQUIRED_TRANSPORT.toString())
            .param("status", DEFAULT_STATUS.toString())
        ).andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void createEventWithExistingId_ShouldReturn400() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc.perform(post("/api/events")
            .param("id", "1")
            .param("name", DEFAULT_NAME)
            .param("description", DEFAULT_DESCRIPTION)
            .param("remarks", DEFAULT_REMARKS)
            .param("venue", DEFAULT_VENUE)
            .param("startDate", DEFAULT_START_DATE.toString())
            .param("endDate", DEFAULT_END_DATE.toString())
            .param("fee", DEFAULT_FEE.toString())
            .param("requiredTransport", DEFAULT_REQUIRED_TRANSPORT.toString())
            .param("status", DEFAULT_STATUS.toString())
        ).andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void updateEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event
        Event updatedEvent = eventRepository.findById(event.getId()).get();

        restEventMockMvc.perform(put("/api/events")
            .param("id", updatedEvent.getId().toString())
            .param("name", UPDATED_NAME)
            .param("description", UPDATED_DESCRIPTION)
            .param("remarks", UPDATED_REMARKS)
            .param("venue", UPDATED_VENUE)
            .param("startDate", UPDATED_START_DATE.toString())
            .param("endDate", UPDATED_END_DATE.toString())
            .param("fee", UPDATED_FEE.toString())
            .param("requiredTransport", UPDATED_REQUIRED_TRANSPORT.toString())
            .param("status", UPDATED_STATUS.toString())
        ).andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvent.getRemarks()).isEqualTo(UPDATED_REMARKS);
        assertThat(testEvent.getVenue()).isEqualTo(UPDATED_VENUE);
        assertThat(testEvent.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testEvent.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testEvent.getFee()).isEqualTo(UPDATED_FEE);
        assertThat(testEvent.isRequiredTransport()).isEqualTo(UPDATED_REQUIRED_TRANSPORT);
        assertThat(testEvent.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void updateEventWithEventImage_ShouldSuccess() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event
        Event updatedEvent = eventRepository.findById(event.getId()).get();
        Event testUpdateEvent = createUpdatedEntity();
        testUpdateEvent.setId(updatedEvent.getId());
        EventDTO eventDTO = eventMapper.toDto(testUpdateEvent);
        eventDTO.setMultipartFile(MOCK_MULTIPART_FILE);

        restEventMockMvc.perform(put("/api/events")
            .contentType(MediaType.MULTIPART_FORM_DATA)
//            .content(TestUtil.convertObjectToJsonBytes(eventDTO))
//            .file(MOCK_MULTIPART_FILE)
            .param("id", updatedEvent.getId().toString())
            .param("name", UPDATED_NAME)
            .param("description", UPDATED_DESCRIPTION)
            .param("remarks", UPDATED_REMARKS)
            .param("venue", UPDATED_VENUE)
            .param("startDate", UPDATED_START_DATE.toString())
            .param("endDate", UPDATED_END_DATE.toString())
            .param("fee", UPDATED_FEE.toString())
            .param("requiredTransport", UPDATED_REQUIRED_TRANSPORT.toString())
            .param("status", UPDATED_STATUS.toString())
            .requestAttr("multipartFile", MOCK_MULTIPART_FILE)
        ).andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvent.getRemarks()).isEqualTo(UPDATED_REMARKS);
        assertThat(testEvent.getVenue()).isEqualTo(UPDATED_VENUE);
        assertThat(testEvent.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testEvent.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testEvent.getFee()).isEqualTo(UPDATED_FEE);
        assertThat(testEvent.isRequiredTransport()).isEqualTo(UPDATED_REQUIRED_TRANSPORT);
        assertThat(testEvent.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testEvent.getImageStorageId()).isEqualTo(DEFAULT_IMAGE_STORAGE_ID);
    }

    @Test
    @Transactional
    @WithEventHead
    public void updateEventWithEventHead_ShouldSuccess() throws Exception {
        // Initialize the database
        EventCrew currentEventHead = getEventCrewByCurrentLoginUser();

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event
        Event updatedEvent = eventRepository.findById(currentEventHead.getEventId()).get();
        // Disconnect from session so that the updates on updatedEvent are not directly saved in db

        restEventMockMvc.perform(put("/api/events")
            .param("id", updatedEvent.getId().toString())
            .param("name", UPDATED_NAME)
            .param("description", UPDATED_DESCRIPTION)
            .param("remarks", UPDATED_REMARKS)
            .param("venue", UPDATED_VENUE)
            .param("startDate", UPDATED_START_DATE.toString())
            .param("endDate", UPDATED_END_DATE.toString())
            .param("fee", UPDATED_FEE.toString())
            .param("requiredTransport", UPDATED_REQUIRED_TRANSPORT.toString())
            .param("status", UPDATED_STATUS.toString())
        ).andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvent.getRemarks()).isEqualTo(UPDATED_REMARKS);
        assertThat(testEvent.getVenue()).isEqualTo(UPDATED_VENUE);
        assertThat(testEvent.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testEvent.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testEvent.getFee()).isEqualTo(UPDATED_FEE);
        assertThat(testEvent.isRequiredTransport()).isEqualTo(UPDATED_REQUIRED_TRANSPORT);
        assertThat(testEvent.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    // TODO: Update Event with New Event Image

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void updateNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // Mock user for Authorization Checking
        Optional<User> currentUser = userService.getUserWithAuthorities();
        EventCrewDTO eventCrewDTO = new EventCrewDTO();
        eventCrewDTO.setEventId(event.getId());
        eventCrewDTO.setUserId(currentUser.get().getId());
        eventCrewService.save(eventCrewDTO);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc.perform(put("/api/events")
            .param("id", "1")
            .param("name", UPDATED_NAME)
            .param("description", UPDATED_DESCRIPTION)
            .param("remarks", UPDATED_REMARKS)
            .param("venue", UPDATED_VENUE)
            .param("startDate", UPDATED_START_DATE.toString())
            .param("endDate", UPDATED_END_DATE.toString())
            .param("fee", UPDATED_FEE.toString())
            .param("requiredTransport", UPDATED_REQUIRED_TRANSPORT.toString())
            .param("status", UPDATED_STATUS.toString())
        ).andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void getAllEvents() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList
        restEventMockMvc.perform(get("/api/events?sort=id,desc")
            .with(user("user").password("user").roles("USER")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
            .andExpect(jsonPath("$.[*].venue").value(hasItem(DEFAULT_VENUE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].fee").value(hasItem(DEFAULT_FEE.intValue())))
            .andExpect(jsonPath("$.[*].requiredTransport").value(hasItem(DEFAULT_REQUIRED_TRANSPORT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getAllUpcomingEvents() throws Exception {
        // Initialize the database
        Event savedEvent = createEntity();
        savedEvent.setStartDate(Instant.now().plus(20, ChronoUnit.DAYS));
        eventRepository.saveAndFlush(savedEvent);

        // Get all the eventList
        restEventMockMvc.perform(get("/api/events/upcoming"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
            .andExpect(jsonPath("$.[*].venue").value(hasItem(DEFAULT_VENUE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(savedEvent.getStartDate().toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].fee").value(hasItem(DEFAULT_FEE.intValue())))
            .andExpect(jsonPath("$.[*].requiredTransport").value(hasItem(DEFAULT_REQUIRED_TRANSPORT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getAllUpcomingEvents_WithCancelledStatus() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        // Initialize the database
        Event savedEvent = createEntity();
        savedEvent.setStartDate(Instant.now().plus(20, ChronoUnit.DAYS));
        savedEvent.setStatus(CANCELLED_STATUS);
        eventRepository.saveAndFlush(savedEvent);

        // Get all the eventList
        restEventMockMvc.perform(get("/api/events/upcoming"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(not(hasItem(savedEvent.getId().intValue()))));

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    public void getAllPastEvents() throws Exception {
        // Initialize the database
        Event savedEvent = createEntity();
        savedEvent.setStartDate(Instant.now().minus(20, ChronoUnit.DAYS));
        eventRepository.saveAndFlush(savedEvent);

        // Get all the eventList
        restEventMockMvc.perform(get("/api/events/past"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
            .andExpect(jsonPath("$.[*].venue").value(hasItem(DEFAULT_VENUE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(savedEvent.getStartDate().toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].fee").value(hasItem(DEFAULT_FEE.intValue())))
            .andExpect(jsonPath("$.[*].requiredTransport").value(hasItem(DEFAULT_REQUIRED_TRANSPORT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getAllPastEvents_WithCancelledStatus() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        // Initialize the database
        Event savedEvent = createEntity();
        savedEvent.setStatus(CANCELLED_STATUS);
        savedEvent.setStartDate(Instant.now().minus(20, ChronoUnit.DAYS));
        eventRepository.saveAndFlush(savedEvent);

        // Get all the eventList
        restEventMockMvc.perform(get("/api/events/past"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(not(hasItem(savedEvent.getId().intValue()))));

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
    }


    @Test
    @Transactional
    public void getAllEventsWithDateRange() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList with date range
        restEventMockMvc.perform(get("/api/events?page=0&size=3&sort=startDate,desc&from="+DEFAULT_START_DATE.minusSeconds(60 * 60 * 24)+"&to="+DEFAULT_START_DATE.plusSeconds(60 * 60 * 24))
            .with(user("user").password("user").roles("USER")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
            .andExpect(jsonPath("$.[*].venue").value(hasItem(DEFAULT_VENUE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].fee").value(hasItem(DEFAULT_FEE.intValue())))
            .andExpect(jsonPath("$.[*].requiredTransport").value(hasItem(DEFAULT_REQUIRED_TRANSPORT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }


    @Test
    @Transactional
    public void getAllEventsWithNonExistentDateRange() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList with date range but should return empty array
        restEventMockMvc.perform(get("/api/events?page=0&size=3&sort=startDate,desc&from="+Instant.now().minusSeconds(60 * 60 * 24)+"&to="+Instant.now().plusSeconds(60 * 60 * 24))
            .with(user("user").password("user").roles("USER")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", event.getId())
            .with(user("user").password("user").roles("USER")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS))
            .andExpect(jsonPath("$.venue").value(DEFAULT_VENUE))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.fee").value(DEFAULT_FEE.intValue()))
            .andExpect(jsonPath("$.requiredTransport").value(DEFAULT_REQUIRED_TRANSPORT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", Long.MAX_VALUE)
            .with(user("user").password("user").roles("USER")))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void cancelEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Cancel the event
        restEventMockMvc.perform(put("/api/event/{eventId}/deactivate", event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS.toString()))
            .andExpect(jsonPath("$.venue").value(DEFAULT_VENUE.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.fee").value(DEFAULT_FEE.intValue()))
            .andExpect(jsonPath("$.requiredTransport").value(DEFAULT_REQUIRED_TRANSPORT.booleanValue()))
            .andExpect(jsonPath("$.status").value(CANCELLED_STATUS.toString()));
    }

    @Test
    @Transactional
    public void cancelEvent_AsNonAdminUser_ShouldReturn403() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Cancel the event
        restEventMockMvc.perform(put("/api/event/{eventId}/deactivate", event.getId()))
            .andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void cancelEvent_WithNonExistingEventId_ShouldReturn400() throws Exception {

        restEventMockMvc.perform(put("/api/event/{eventId}/deactivate", Long.MAX_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithCurrentCCAdministrator
    public void deleteEvent() throws Exception {
        // Initialize the database
        YearSession savedYearSession = initYearSessionDB();
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeDelete = eventRepository.findAll().size();

        // Delete the event
        restEventMockMvc.perform(delete("/api/events/{id}", event.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);
        event2.setId(2L);
        assertThat(event1).isNotEqualTo(event2);
        event1.setId(null);
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventDTO.class);
        EventDTO eventDTO1 = new EventDTO();
        eventDTO1.setId(1L);
        EventDTO eventDTO2 = new EventDTO();
        assertThat(eventDTO1).isNotEqualTo(eventDTO2);
        eventDTO2.setId(eventDTO1.getId());
        assertThat(eventDTO1).isEqualTo(eventDTO2);
        eventDTO2.setId(2L);
        assertThat(eventDTO1).isNotEqualTo(eventDTO2);
        eventDTO1.setId(null);
        assertThat(eventDTO1).isNotEqualTo(eventDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(eventMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(eventMapper.fromId(null)).isNull();
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
}
