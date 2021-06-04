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
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static com.thirdcc.webapp.web.rest.TestUtil.sameNumber;
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

    private static final String ENTITY_API_URL = "/api/events";

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
    private static final BigDecimal SMALLER_FEE = DEFAULT_FEE.subtract(BigDecimal.ONE);
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

//    @MockBean
//    private ImageStorageService imageStorageService;

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
            .with(request -> {
                request.setMethod("POST");
                return request;
            })
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

        restEventMockMvc.perform(multipart("/api/events")
            .file(MOCK_MULTIPART_FILE)
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
            .with(request -> {
                request.setMethod("PUT");
                    return request;
            })
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
    void getEventsByIdFiltering() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        Long id = event.getId();

        defaultEventShouldBeFound("id.equals=" + id);
        defaultEventShouldNotBeFound("id.notEquals=" + id);

        defaultEventShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEventShouldNotBeFound("id.greaterThan=" + id);

        defaultEventShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEventShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where name equals to DEFAULT_NAME
        defaultEventShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the eventList where name equals to UPDATED_NAME
        defaultEventShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEventsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where name not equals to DEFAULT_NAME
        defaultEventShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the eventList where name not equals to UPDATED_NAME
        defaultEventShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEventsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where name in DEFAULT_NAME or UPDATED_NAME
        defaultEventShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the eventList where name equals to UPDATED_NAME
        defaultEventShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEventsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where name is not null
        defaultEventShouldBeFound("name.specified=true");

        // Get all the eventList where name is null
        defaultEventShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByNameContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where name contains DEFAULT_NAME
        defaultEventShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the eventList where name contains UPDATED_NAME
        defaultEventShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEventsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where name does not contain DEFAULT_NAME
        defaultEventShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the eventList where name does not contain UPDATED_NAME
        defaultEventShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEventsByRemarksIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where remarks equals to DEFAULT_REMARKS
        defaultEventShouldBeFound("remarks.equals=" + DEFAULT_REMARKS);

        // Get all the eventList where remarks equals to UPDATED_REMARKS
        defaultEventShouldNotBeFound("remarks.equals=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllEventsByRemarksIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where remarks not equals to DEFAULT_REMARKS
        defaultEventShouldNotBeFound("remarks.notEquals=" + DEFAULT_REMARKS);

        // Get all the eventList where remarks not equals to UPDATED_REMARKS
        defaultEventShouldBeFound("remarks.notEquals=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllEventsByRemarksIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where remarks in DEFAULT_REMARKS or UPDATED_REMARKS
        defaultEventShouldBeFound("remarks.in=" + DEFAULT_REMARKS + "," + UPDATED_REMARKS);

        // Get all the eventList where remarks equals to UPDATED_REMARKS
        defaultEventShouldNotBeFound("remarks.in=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllEventsByRemarksIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where remarks is not null
        defaultEventShouldBeFound("remarks.specified=true");

        // Get all the eventList where remarks is null
        defaultEventShouldNotBeFound("remarks.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByRemarksContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where remarks contains DEFAULT_REMARKS
        defaultEventShouldBeFound("remarks.contains=" + DEFAULT_REMARKS);

        // Get all the eventList where remarks contains UPDATED_REMARKS
        defaultEventShouldNotBeFound("remarks.contains=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllEventsByRemarksNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where remarks does not contain DEFAULT_REMARKS
        defaultEventShouldNotBeFound("remarks.doesNotContain=" + DEFAULT_REMARKS);

        // Get all the eventList where remarks does not contain UPDATED_REMARKS
        defaultEventShouldBeFound("remarks.doesNotContain=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllEventsByVenueIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where venue equals to DEFAULT_VENUE
        defaultEventShouldBeFound("venue.equals=" + DEFAULT_VENUE);

        // Get all the eventList where venue equals to UPDATED_VENUE
        defaultEventShouldNotBeFound("venue.equals=" + UPDATED_VENUE);
    }

    @Test
    @Transactional
    void getAllEventsByVenueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where venue not equals to DEFAULT_VENUE
        defaultEventShouldNotBeFound("venue.notEquals=" + DEFAULT_VENUE);

        // Get all the eventList where venue not equals to UPDATED_VENUE
        defaultEventShouldBeFound("venue.notEquals=" + UPDATED_VENUE);
    }

    @Test
    @Transactional
    void getAllEventsByVenueIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where venue in DEFAULT_VENUE or UPDATED_VENUE
        defaultEventShouldBeFound("venue.in=" + DEFAULT_VENUE + "," + UPDATED_VENUE);

        // Get all the eventList where venue equals to UPDATED_VENUE
        defaultEventShouldNotBeFound("venue.in=" + UPDATED_VENUE);
    }

    @Test
    @Transactional
    void getAllEventsByVenueIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where venue is not null
        defaultEventShouldBeFound("venue.specified=true");

        // Get all the eventList where venue is null
        defaultEventShouldNotBeFound("venue.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByVenueContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where venue contains DEFAULT_VENUE
        defaultEventShouldBeFound("venue.contains=" + DEFAULT_VENUE);

        // Get all the eventList where venue contains UPDATED_VENUE
        defaultEventShouldNotBeFound("venue.contains=" + UPDATED_VENUE);
    }

    @Test
    @Transactional
    void getAllEventsByVenueNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where venue does not contain DEFAULT_VENUE
        defaultEventShouldNotBeFound("venue.doesNotContain=" + DEFAULT_VENUE);

        // Get all the eventList where venue does not contain UPDATED_VENUE
        defaultEventShouldBeFound("venue.doesNotContain=" + UPDATED_VENUE);
    }

    @Test
    @Transactional
    void getAllEventsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startDate equals to DEFAULT_START_DATE
        defaultEventShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the eventList where startDate equals to UPDATED_START_DATE
        defaultEventShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllEventsByStartDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startDate not equals to DEFAULT_START_DATE
        defaultEventShouldNotBeFound("startDate.notEquals=" + DEFAULT_START_DATE);

        // Get all the eventList where startDate not equals to UPDATED_START_DATE
        defaultEventShouldBeFound("startDate.notEquals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllEventsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultEventShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the eventList where startDate equals to UPDATED_START_DATE
        defaultEventShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllEventsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startDate is not null
        defaultEventShouldBeFound("startDate.specified=true");

        // Get all the eventList where startDate is null
        defaultEventShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endDate equals to DEFAULT_END_DATE
        defaultEventShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the eventList where endDate equals to UPDATED_END_DATE
        defaultEventShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllEventsByEndDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endDate not equals to DEFAULT_END_DATE
        defaultEventShouldNotBeFound("endDate.notEquals=" + DEFAULT_END_DATE);

        // Get all the eventList where endDate not equals to UPDATED_END_DATE
        defaultEventShouldBeFound("endDate.notEquals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllEventsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultEventShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the eventList where endDate equals to UPDATED_END_DATE
        defaultEventShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllEventsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endDate is not null
        defaultEventShouldBeFound("endDate.specified=true");

        // Get all the eventList where endDate is null
        defaultEventShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByFeeIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where fee equals to DEFAULT_FEE
        defaultEventShouldBeFound("fee.equals=" + DEFAULT_FEE);

        // Get all the eventList where fee equals to UPDATED_FEE
        defaultEventShouldNotBeFound("fee.equals=" + UPDATED_FEE);
    }

    @Test
    @Transactional
    void getAllEventsByFeeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where fee not equals to DEFAULT_FEE
        defaultEventShouldNotBeFound("fee.notEquals=" + DEFAULT_FEE);

        // Get all the eventList where fee not equals to UPDATED_FEE
        defaultEventShouldBeFound("fee.notEquals=" + UPDATED_FEE);
    }

    @Test
    @Transactional
    void getAllEventsByFeeIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where fee in DEFAULT_FEE or UPDATED_FEE
        defaultEventShouldBeFound("fee.in=" + DEFAULT_FEE + "," + UPDATED_FEE);

        // Get all the eventList where fee equals to UPDATED_FEE
        defaultEventShouldNotBeFound("fee.in=" + UPDATED_FEE);
    }

    @Test
    @Transactional
    void getAllEventsByFeeIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where fee is not null
        defaultEventShouldBeFound("fee.specified=true");

        // Get all the eventList where fee is null
        defaultEventShouldNotBeFound("fee.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByFeeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where fee is greater than or equal to DEFAULT_FEE
        defaultEventShouldBeFound("fee.greaterThanOrEqual=" + DEFAULT_FEE);

        // Get all the eventList where fee is greater than or equal to UPDATED_FEE
        defaultEventShouldNotBeFound("fee.greaterThanOrEqual=" + UPDATED_FEE);
    }

    @Test
    @Transactional
    void getAllEventsByFeeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where fee is less than or equal to DEFAULT_FEE
        defaultEventShouldBeFound("fee.lessThanOrEqual=" + DEFAULT_FEE);

        // Get all the eventList where fee is less than or equal to SMALLER_FEE
        defaultEventShouldNotBeFound("fee.lessThanOrEqual=" + SMALLER_FEE);
    }

    @Test
    @Transactional
    void getAllEventsByFeeIsLessThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where fee is less than DEFAULT_FEE
        defaultEventShouldNotBeFound("fee.lessThan=" + DEFAULT_FEE);

        // Get all the eventList where fee is less than UPDATED_FEE
        defaultEventShouldBeFound("fee.lessThan=" + UPDATED_FEE);
    }

    @Test
    @Transactional
    void getAllEventsByFeeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where fee is greater than DEFAULT_FEE
        defaultEventShouldNotBeFound("fee.greaterThan=" + DEFAULT_FEE);

        // Get all the eventList where fee is greater than SMALLER_FEE
        defaultEventShouldBeFound("fee.greaterThan=" + SMALLER_FEE);
    }

    @Test
    @Transactional
    void getAllEventsByRequiredTransportIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where requiredTransport equals to DEFAULT_REQUIRED_TRANSPORT
        defaultEventShouldBeFound("requiredTransport.equals=" + DEFAULT_REQUIRED_TRANSPORT);

        // Get all the eventList where requiredTransport equals to UPDATED_REQUIRED_TRANSPORT
        defaultEventShouldNotBeFound("requiredTransport.equals=" + UPDATED_REQUIRED_TRANSPORT);
    }

    @Test
    @Transactional
    void getAllEventsByRequiredTransportIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where requiredTransport not equals to DEFAULT_REQUIRED_TRANSPORT
        defaultEventShouldNotBeFound("requiredTransport.notEquals=" + DEFAULT_REQUIRED_TRANSPORT);

        // Get all the eventList where requiredTransport not equals to UPDATED_REQUIRED_TRANSPORT
        defaultEventShouldBeFound("requiredTransport.notEquals=" + UPDATED_REQUIRED_TRANSPORT);
    }

    @Test
    @Transactional
    void getAllEventsByRequiredTransportIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where requiredTransport in DEFAULT_REQUIRED_TRANSPORT or UPDATED_REQUIRED_TRANSPORT
        defaultEventShouldBeFound("requiredTransport.in=" + DEFAULT_REQUIRED_TRANSPORT + "," + UPDATED_REQUIRED_TRANSPORT);

        // Get all the eventList where requiredTransport equals to UPDATED_REQUIRED_TRANSPORT
        defaultEventShouldNotBeFound("requiredTransport.in=" + UPDATED_REQUIRED_TRANSPORT);
    }

    @Test
    @Transactional
    void getAllEventsByRequiredTransportIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where requiredTransport is not null
        defaultEventShouldBeFound("requiredTransport.specified=true");

        // Get all the eventList where requiredTransport is null
        defaultEventShouldNotBeFound("requiredTransport.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where status equals to DEFAULT_STATUS
        defaultEventShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the eventList where status equals to UPDATED_STATUS
        defaultEventShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEventsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where status not equals to DEFAULT_STATUS
        defaultEventShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the eventList where status not equals to UPDATED_STATUS
        defaultEventShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEventsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultEventShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the eventList where status equals to UPDATED_STATUS
        defaultEventShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEventsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where status is not null
        defaultEventShouldBeFound("status.specified=true");

        // Get all the eventList where status is null
        defaultEventShouldNotBeFound("status.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventShouldBeFound(String filter) throws Exception {
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
            .andExpect(jsonPath("$.[*].venue").value(hasItem(DEFAULT_VENUE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].fee").value(hasItem(sameNumber(DEFAULT_FEE))))
            .andExpect(jsonPath("$.[*].requiredTransport").value(hasItem(DEFAULT_REQUIRED_TRANSPORT.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventShouldNotBeFound(String filter) throws Exception {
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(content().string("0"));
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
