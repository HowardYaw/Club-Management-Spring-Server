package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.WithCurrentCCAdministrator;
import com.thirdcc.webapp.annotations.authorization.WithEventHead;
import com.thirdcc.webapp.annotations.authorization.WithUnauthenticatedMockUser;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventActivity;
import com.thirdcc.webapp.domain.EventAttendee;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.UserUniInfo;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.domain.enumeration.UserUniStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.EventAttendeeRepository;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.repository.UserUniInfoRepository;
import com.thirdcc.webapp.service.EventAttendeeService;
import com.thirdcc.webapp.service.UserService;
import com.thirdcc.webapp.service.dto.EventAttendeeDTO;
import com.thirdcc.webapp.service.impl.EventAttendeeServiceImpl;
import com.thirdcc.webapp.service.mapper.EventAttendeeMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link EventAttendeeResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@InitYearSession
@WithMockUser(username = "admin", roles = "ADMIN")
public class EventAttendeeResourceIT {
    private final Logger log = LoggerFactory.getLogger(EventAttendeeServiceImpl.class);

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long UPDATED_EVENT_ID = 2L;

    private static final Boolean DEFAULT_PROVIDE_TRANSPORT = false;
    private static final Boolean UPDATED_PROVIDE_TRANSPORT = true;

    private static final String DEFAULT_EVENT_NAME = "DEFAULT_EVENT_NAME";
    private static final String DEFAULT_EVENT_DESCRIPTION = "DEFAULT_EVENT_DESCRIPTION";
    private static final String DEFAULT_EVENT_REMARKS = "DEFAULT_EVENT_REMARKS";
    private static final String DEFAULT_EVENT_VENUE = "DEFAULT_EVENT_VENUE";
    private static final Instant DEFAULT_EVENT_START_DATE = Instant.now().minus(5, ChronoUnit.DAYS);
    private static final Instant DEFAULT_EVENT_END_DATE = Instant.now().plus(5, ChronoUnit.DAYS);
    private static final BigDecimal DEFAULT_EVENT_FEE = new BigDecimal(2123);
    private static final Boolean DEFAULT_EVENT_REQUIRED_TRANSPORT = Boolean.TRUE;
    private static final EventStatus DEFAULT_EVENT_STATUS = EventStatus.OPEN;

    // User Uni Info Default Value
    private static final String DEFAULT_FACULTY = "AAAAAAAAAA";
    private static final String DEFAULT_PROGRAM = "AAAAAAAAAA";
    private static final String DEFAULT_YEAR_SESSION = "AAAAAAAAAA";
    private static final Integer DEFAULT_INTAKE_SEMESTER = 1;
    private static final BigDecimal DEFAULT_YEAR_OF_STUDY = new BigDecimal(1);
    private static final String DEFAULT_STAY_IN = "AAAAAAAAAA";
    private static final UserUniStatus DEFAULT_STATUS = UserUniStatus.GRADUATED;

    private static User user;

    @Autowired
    private UserUniInfoRepository userUniInfoRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventAttendeeRepository eventAttendeeRepository;

    @Autowired
    private EventAttendeeMapper eventAttendeeMapper;

    @Autowired
    private EventAttendeeService eventAttendeeService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventAttendeeMockMvc;

    private EventAttendee eventAttendee;

    private Event event;

    private UserUniInfo userUniInfo;

    @Autowired
    private UserService userService;

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
    public static EventAttendee createEventAttendeeEntity(EntityManager em) {
        EventAttendee eventAttendee = new EventAttendee()
            .userId(DEFAULT_USER_ID)
            .eventId(DEFAULT_EVENT_ID)
            .provideTransport(DEFAULT_PROVIDE_TRANSPORT);
        return eventAttendee;
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

    public static EventAttendeeDTO createDefaultEventAttendeeDTO() {
        EventAttendeeDTO eventAttendeeDTO = new EventAttendeeDTO();
        eventAttendeeDTO.setProvideTransport(DEFAULT_PROVIDE_TRANSPORT);
        return eventAttendeeDTO;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventAttendee createUpdatedEntity(EntityManager em) {
        EventAttendee eventAttendee = new EventAttendee()
            .userId(UPDATED_USER_ID)
            .eventId(UPDATED_EVENT_ID)
            .provideTransport(UPDATED_PROVIDE_TRANSPORT);
        return eventAttendee;
    }

    public static UserUniInfo createUserUniInfoEntity() {
        UserUniInfo userUniInfo = new UserUniInfo()
            .userId(DEFAULT_USER_ID)
            .faculty(DEFAULT_FACULTY)
            .program(DEFAULT_PROGRAM)
            .yearSession(DEFAULT_YEAR_SESSION)
            .intakeSemester(DEFAULT_INTAKE_SEMESTER)
            .yearOfStudy(DEFAULT_YEAR_OF_STUDY)
            .stayIn(DEFAULT_STAY_IN)
            .status(DEFAULT_STATUS);
        return userUniInfo;
    }

    @BeforeEach
    public void initTest() {
        event = createEventEntity(em);
        eventAttendee = createEventAttendeeEntity(em);
    }

    @AfterEach
    public void cleanUp() {
        eventAttendeeRepository.deleteAll();
        eventRepository.deleteAll();
        userUniInfoRepository.deleteAll();
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventAttendee() throws Exception {
        Event savedEvent = initEventDB();
        int databaseSizeBeforeCreate = eventAttendeeRepository.findAll().size();

        // Create the EventAttendee
        EventAttendeeDTO eventAttendeeDTO = createDefaultEventAttendeeDTO();
        eventAttendeeDTO.setEventId(savedEvent.getId());
        eventAttendeeDTO.setUserId(user.getId());

        restEventAttendeeMockMvc.perform(post("/api/event-attendees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventAttendeeDTO)))
            .andExpect(status().isCreated());

        // Validate the EventAttendee in the database
        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeCreate + 1);
        EventAttendee testEventAttendee = eventAttendeeList.get(eventAttendeeList.size() - 1);
        assertThat(testEventAttendee.getUserId()).isEqualTo(user.getId());
        assertThat(testEventAttendee.getEventId()).isEqualTo(savedEvent.getId());
        assertThat(testEventAttendee.isProvideTransport()).isEqualTo(DEFAULT_PROVIDE_TRANSPORT);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventAttendee_WithNonExistingUserId_ShouldThrow400() throws Exception {
        Event savedEvent = initEventDB();
        int databaseSizeBeforeCreate = eventAttendeeRepository.findAll().size();

        // Create the EventAttendee
        EventAttendeeDTO eventAttendeeDTO = createDefaultEventAttendeeDTO();
        eventAttendeeDTO.setUserId(Long.MAX_VALUE);
        eventAttendeeDTO.setEventId(savedEvent.getId());

        restEventAttendeeMockMvc.perform(post("/api/event-attendees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventAttendeeDTO)))
            .andExpect(status().isBadRequest());

        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventAttendee_WithExistedAttendee_ShouldThrow400() throws Exception {
        EventAttendee eventAttendee = initEventAttendeeDB();
        int databaseSizeBeforeCreate = eventAttendeeRepository.findAll().size();

        // Create the EventAttendee
        EventAttendeeDTO eventAttendeeDTO = createDefaultEventAttendeeDTO();
        eventAttendeeDTO.setUserId(eventAttendee.getUserId());
        eventAttendeeDTO.setEventId(eventAttendee.getEventId());

        restEventAttendeeMockMvc.perform(post("/api/event-attendees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventAttendeeDTO)))
            .andExpect(status().isBadRequest());

        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventAttendee_WithNonExistingEvent_ShouldThrow400() throws Exception {
        int databaseSizeBeforeCreate = eventAttendeeRepository.findAll().size();

        EventAttendeeDTO eventAttendeeDTO = createDefaultEventAttendeeDTO();
        eventAttendeeDTO.setUserId(user.getId());
        eventAttendeeDTO.setEventId(Long.MAX_VALUE);

        restEventAttendeeMockMvc.perform(post("/api/event-attendees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventAttendeeDTO)))
            .andExpect(status().isBadRequest());

        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventAttendee_WithEventEnded_ShouldThrow400() throws Exception {
        event.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS));
        Event savedEvent = initEventDB();

        int databaseSizeBeforeCreate = eventAttendeeRepository.findAll().size();

        assertThat(savedEvent.getEndDate()).isBefore(Instant.now());

        EventAttendeeDTO eventAttendeeDTO = createDefaultEventAttendeeDTO();
        eventAttendeeDTO.setUserId(user.getId());
        eventAttendeeDTO.setEventId(savedEvent.getId());

        restEventAttendeeMockMvc.perform(post("/api/event-attendees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventAttendeeDTO)))
            .andExpect(status().isBadRequest());

        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventAttendee_WithEventClosed_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CLOSED);
        Event savedEvent = initEventDB();

        int databaseSizeBeforeCreate = eventAttendeeRepository.findAll().size();

        EventAttendeeDTO eventAttendeeDTO = createDefaultEventAttendeeDTO();
        eventAttendeeDTO.setUserId(user.getId());
        eventAttendeeDTO.setEventId(savedEvent.getId());

        assertThat(savedEvent.getStatus()).isEqualByComparingTo(EventStatus.CLOSED);

        restEventAttendeeMockMvc.perform(post("/api/event-attendees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventAttendeeDTO)))
            .andExpect(status().isBadRequest());

        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventAttendee_WithEventCancelled_ShouldThrow400() throws Exception {
        event.setStatus(EventStatus.CANCELLED);
        Event savedEvent = initEventDB();

        int databaseSizeBeforeCreate = eventAttendeeRepository.findAll().size();

        EventAttendeeDTO eventAttendeeDTO = createDefaultEventAttendeeDTO();
        eventAttendeeDTO.setUserId(user.getId());
        eventAttendeeDTO.setEventId(savedEvent.getId());

        assertThat(savedEvent.getStatus()).isEqualByComparingTo(EventStatus.CANCELLED);

        restEventAttendeeMockMvc.perform(post("/api/event-attendees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventAttendeeDTO)))
            .andExpect(status().isBadRequest());

        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void createEventAttendeeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = eventAttendeeRepository.findAll().size();

        // Create the EventAttendee with an existing ID
        eventAttendee.setId(1L);
        EventAttendeeDTO eventAttendeeDTO = eventAttendeeMapper.toDto(eventAttendee);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventAttendeeMockMvc.perform(post("/api/event-attendees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventAttendeeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventAttendee in the database
        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void getAllEventAttendees() throws Exception {
        // Initialize the database
        eventAttendeeRepository.saveAndFlush(eventAttendee);

        // Get all the eventAttendeeList
        restEventAttendeeMockMvc.perform(get("/api/event-attendees?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventAttendee.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].eventId").value(hasItem(DEFAULT_EVENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].provideTransport").value(hasItem(DEFAULT_PROVIDE_TRANSPORT.booleanValue())));
    }

    @Test
    @WithCurrentCCAdministrator
    public void getEventAttendee() throws Exception {
        // Initialize the database
        eventAttendeeRepository.saveAndFlush(eventAttendee);

        // Get the eventAttendee
        restEventAttendeeMockMvc.perform(get("/api/event-attendees/{id}", eventAttendee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(eventAttendee.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.eventId").value(DEFAULT_EVENT_ID.intValue()))
            .andExpect(jsonPath("$.provideTransport").value(DEFAULT_PROVIDE_TRANSPORT.booleanValue()));
    }

    @Test
    @WithCurrentCCAdministrator
    public void getAllEventAttendees_WithEventId() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        EventAttendee savedEventAttendee = initEventAttendeeDB();
        userUniInfo = createUserUniInfoEntity();
        initUserUniInfoDB();

        // Get the eventAttendee
        restEventAttendeeMockMvc.perform(get("/api/event-attendees/event/{eventId}?sort=id,desc", savedEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(savedEventAttendee.getId().intValue()))
            .andExpect(jsonPath("$.[*].userId").value(user.getId().intValue()))
            .andExpect(jsonPath("$.[*].eventId").value(savedEvent.getId().intValue()))
            .andExpect(jsonPath("$.[*].provideTransport").value(DEFAULT_PROVIDE_TRANSPORT.booleanValue()))
            .andExpect(jsonPath("$.[*].yearSession").value(DEFAULT_YEAR_SESSION));
    }

    @Test
    @WithCurrentCCAdministrator
    public void getAllEventAttendees_WithEventId_WithEmptyUserUniInfoDB() throws Exception {
        // Initialize the database
        Event savedEvent = initEventDB();
        EventAttendee savedEventAttendee = initEventAttendeeDB();

        // Get the eventAttendee
        restEventAttendeeMockMvc.perform(get("/api/event-attendees/event/{eventId}?sort=id,desc", savedEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(savedEventAttendee.getId().intValue()))
            .andExpect(jsonPath("$.[*].userId").value(user.getId().intValue()))
            .andExpect(jsonPath("$.[*].eventId").value(savedEvent.getId().intValue()))
            .andExpect(jsonPath("$.[*].provideTransport").value(DEFAULT_PROVIDE_TRANSPORT.booleanValue()))
            .andExpect(jsonPath("$.[*].yearSession").value(""));
    }

    @Test
    @WithCurrentCCAdministrator
    public void getAllEventAttendees_WithNonExistingEventId_ShouldThrow400() throws Exception {
        restEventAttendeeMockMvc.perform(get("/api/event-attendees/event/{eventId}?sort=id,desc", Long.MAX_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void getAllEventAttendees_WithEventId_NonAdmin_ShouldThrow403() throws Exception {
        user = getLoggedInUser();
        // Initialize the database
        Event savedEvent = initEventDB();
        EventAttendee savedEventAttendee = initEventAttendeeDB();

        // Get the eventAttendee
        restEventAttendeeMockMvc.perform(get("/api/event-attendees/event/{eventId}?sort=id,desc", savedEvent.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithCurrentCCAdministrator
    public void getNonExistingEventAttendee() throws Exception {
        // Get the eventAttendee
        restEventAttendeeMockMvc.perform(get("/api/event-attendees/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithCurrentCCAdministrator
    public void updateNonExistingEventAttendee() throws Exception {
        int databaseSizeBeforeUpdate = eventAttendeeRepository.findAll().size();

        // Create the EventAttendee
        EventAttendeeDTO eventAttendeeDTO = eventAttendeeMapper.toDto(eventAttendee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventAttendeeMockMvc.perform(put("/api/event-attendees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventAttendeeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventAttendee in the database
        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithCurrentCCAdministrator
    public void deleteEventAttendee() throws Exception {
        // Initialize the database
        eventAttendeeRepository.saveAndFlush(eventAttendee);

        int databaseSizeBeforeDelete = eventAttendeeRepository.findAll().size();

        // Delete the eventAttendee
        restEventAttendeeMockMvc.perform(delete("/api/event-attendees/{id}", eventAttendee.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeDelete - 1);
    }


    @Test
    public void getEventAttendeeByEventIdAndUserId() throws Exception {
        //Initialize the event and eventAttendee
        Event savedEvent = initEventDB();
        initEventAttendeeDB();

        //Save events and attendee
        restEventAttendeeMockMvc.perform(get("/api/event-attendees/event/{eventId}/user/{userId}", savedEvent.getId(), user.getId() ))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(eventAttendee.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(user.getId()))
            .andExpect(jsonPath("$.eventId").value(savedEvent.getId()))
            .andExpect(jsonPath("$.provideTransport").value(DEFAULT_PROVIDE_TRANSPORT.booleanValue()));

    }

    @Test
    public void getEventAttendeeByEventIdAndUserId_withNonExistingUser_ShouldReturnOk() throws Exception {
        //Initialize the event and eventAttendee
        Event savedEvent = initEventDB();
        initEventAttendeeDB();

        //Save events and attendee
        restEventAttendeeMockMvc.perform(get("/api/event-attendees/event/{eventId}/user/{userId}", savedEvent.getId(), Long.MAX_VALUE ))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(nullValue()))
            .andExpect(jsonPath("$.userId").value(nullValue()))
            .andExpect(jsonPath("$.eventId").value(nullValue()))
            .andExpect(jsonPath("$.provideTransport").value(nullValue()));;

    }

    @Test
    public void getEventAttendeeByEventIdAndUserId_withNonExistingEvent_ShouldReturnOk() throws Exception {
        //Initialize the event and eventAttendee
        Event savedEvent = initEventDB();
        initEventAttendeeDB();

        //Save events and attendee
        restEventAttendeeMockMvc.perform(get("/api/event-attendees/event/{eventId}/user/{userId}", Long.MAX_VALUE, user.getId() ))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(nullValue()))
            .andExpect(jsonPath("$.userId").value(nullValue()))
            .andExpect(jsonPath("$.eventId").value(nullValue()))
            .andExpect(jsonPath("$.provideTransport").value(nullValue()));;
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventAttendee.class);
        EventAttendee eventAttendee1 = new EventAttendee();
        eventAttendee1.setId(1L);
        EventAttendee eventAttendee2 = new EventAttendee();
        eventAttendee2.setId(eventAttendee1.getId());
        assertThat(eventAttendee1).isEqualTo(eventAttendee2);
        eventAttendee2.setId(2L);
        assertThat(eventAttendee1).isNotEqualTo(eventAttendee2);
        eventAttendee1.setId(null);
        assertThat(eventAttendee1).isNotEqualTo(eventAttendee2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventAttendeeDTO.class);
        EventAttendeeDTO eventAttendeeDTO1 = new EventAttendeeDTO();
        eventAttendeeDTO1.setId(1L);
        EventAttendeeDTO eventAttendeeDTO2 = new EventAttendeeDTO();
        assertThat(eventAttendeeDTO1).isNotEqualTo(eventAttendeeDTO2);
        eventAttendeeDTO2.setId(eventAttendeeDTO1.getId());
        assertThat(eventAttendeeDTO1).isEqualTo(eventAttendeeDTO2);
        eventAttendeeDTO2.setId(2L);
        assertThat(eventAttendeeDTO1).isNotEqualTo(eventAttendeeDTO2);
        eventAttendeeDTO1.setId(null);
        assertThat(eventAttendeeDTO1).isNotEqualTo(eventAttendeeDTO2);
    }

    @Test
    public void testEntityFromId() {
        assertThat(eventAttendeeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(eventAttendeeMapper.fromId(null)).isNull();
    }

    private Event initEventDB(){
        return eventRepository.saveAndFlush(event);
    }

    private EventAttendee initEventAttendeeDB() {
        user = getLoggedInUser();
        eventAttendee.setEventId(event.getId());
        eventAttendee.setUserId(user.getId());
        return eventAttendeeRepository.saveAndFlush(eventAttendee);
    }

    private UserUniInfo initUserUniInfoDB() {
        user = getLoggedInUser();
        userUniInfo.setUserId(user.getId());
        return userUniInfoRepository.saveAndFlush(userUniInfo);
    }

    private User getLoggedInUser() {
        return userService.getUserWithAuthorities()
            .orElseThrow(() -> new BadRequestException("User not login"));
    }
}
