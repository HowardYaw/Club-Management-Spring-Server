package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Authority;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventAttendee;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.EventAttendeeRepository;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.service.EventAttendeeService;
import com.thirdcc.webapp.service.UserService;
import com.thirdcc.webapp.service.dto.EventAttendeeDTO;
import com.thirdcc.webapp.service.mapper.EventAttendeeMapper;
import com.thirdcc.webapp.web.rest.errors.ExceptionTranslator;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.thirdcc.webapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link EventAttendeeResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@WithMockUser
public class EventAttendeeResourceIT {

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


    private static final String DEFAULT_USER_LOGIN = "johndoe";
    private static final String DEFAULT_USER_PASSWORD = "passjohndoe";
    private static final String DEFAULT_USER_EMAIL = "johndoe@localhost";
    private static final String DEFAULT_USER_FIRSTNAME = "john";
    private static final String DEFAULT_USER_LASTNAME = "doe";
    private static final String DEFAULT_USER_IMAGEURL = "http://placehold.it/50x50";
    private static final String DEFAULT_USER_LANGKEY = "en";
    private static User user;


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
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restEventAttendeeMockMvc;

    private EventAttendee eventAttendee;

    private Event event;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EventAttendeeResource eventAttendeeResource = new EventAttendeeResource(eventAttendeeService);
        this.restEventAttendeeMockMvc = MockMvcBuilders.standaloneSetup(eventAttendeeResource)
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
    public static EventAttendee createEventAttendeeEntity(EntityManager em) {
        EventAttendee eventAttendee = new EventAttendee()
            .userId(user.getId())
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

    @BeforeEach
    public void initTest() {
        event = createEventEntity(em);
        user = userService.getUserWithAuthorities()
            .orElseThrow(()-> new RuntimeException("Could not get user when initiate testing"));
        eventAttendee = createEventAttendeeEntity(em);
    }

    @AfterEach
    public void cleanUp() {
        eventAttendeeRepository.deleteAll();
        userRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    public void getNonExistingEventAttendee() throws Exception {
        // Get the eventAttendee
        restEventAttendeeMockMvc.perform(get("/api/event-attendees/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEventAttendee() throws Exception {
        // Initialize the database
        eventAttendeeRepository.saveAndFlush(eventAttendee);

        int databaseSizeBeforeUpdate = eventAttendeeRepository.findAll().size();

        // Update the eventAttendee
        EventAttendee updatedEventAttendee = eventAttendeeRepository.findById(eventAttendee.getId()).get();
        // Disconnect from session so that the updates on updatedEventAttendee are not directly saved in db
        em.detach(updatedEventAttendee);
        updatedEventAttendee
            .userId(UPDATED_USER_ID)
            .eventId(UPDATED_EVENT_ID)
            .provideTransport(UPDATED_PROVIDE_TRANSPORT);
        EventAttendeeDTO eventAttendeeDTO = eventAttendeeMapper.toDto(updatedEventAttendee);

        restEventAttendeeMockMvc.perform(put("/api/event-attendees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventAttendeeDTO)))
            .andExpect(status().isOk());

        // Validate the EventAttendee in the database
        List<EventAttendee> eventAttendeeList = eventAttendeeRepository.findAll();
        assertThat(eventAttendeeList).hasSize(databaseSizeBeforeUpdate);
        EventAttendee testEventAttendee = eventAttendeeList.get(eventAttendeeList.size() - 1);
        assertThat(testEventAttendee.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testEventAttendee.getEventId()).isEqualTo(UPDATED_EVENT_ID);
        assertThat(testEventAttendee.isProvideTransport()).isEqualTo(UPDATED_PROVIDE_TRANSPORT);
    }

    @Test
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    public void testEntityFromId() {
        assertThat(eventAttendeeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(eventAttendeeMapper.fromId(null)).isNull();
    }

    private Event initEventDB(){
        return eventRepository.saveAndFlush(event);
    }


    private EventAttendee initEventAttendeeDB(Event event, User user) {
        eventAttendee.setEventId(event.getId());
        eventAttendee.setUserId(user.getId());
        return eventAttendeeRepository.saveAndFlush(eventAttendee);
    }


}
