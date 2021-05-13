package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.annotations.authorization.*;
import com.thirdcc.webapp.annotations.init.InitYearSession;
import com.thirdcc.webapp.config.Constants;
import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.domain.enumeration.Gender;
import com.thirdcc.webapp.domain.enumeration.UserUniStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.*;
import com.thirdcc.webapp.security.AuthoritiesConstants;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.MailService;
import com.thirdcc.webapp.service.UserService;
import com.thirdcc.webapp.service.dto.PasswordChangeDTO;
import com.thirdcc.webapp.service.dto.UserDTO;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;
import com.thirdcc.webapp.utils.YearSessionUtils;
import com.thirdcc.webapp.web.rest.vm.KeyAndPasswordVM;
import com.thirdcc.webapp.web.rest.vm.ManagedUserVM;
import org.apache.commons.lang3.RandomStringUtils;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AccountResource} REST controller.
 */
@SpringBootTest(classes = ClubmanagementApp.class)
@AutoConfigureMockMvc
@WithMockUser(value = AccountResourceIT.TEST_USER_LOGIN)
@InitYearSession
public class AccountResourceIT {

    static final String TEST_USER_LOGIN = "test";

    private static final String NORMAL_FIRST_NAME = "NORMAL_FIRST_NAME";
    private static final String NORMAL_IMAGE_URL = "NORMAL_IMAGE_URL";

    // email must be unique
    private static final String NORMAL_EMAIL_1 = "normal1@localhost.testing";
    private static final String NORMAL_EMAIL_2 = "normal2@localhost.testing";
    private static final String NORMAL_EMAIL_3 = "normal3@localhost.testing";

    private static final String CC_HEAD_FIRST_NAME = "CC_HEAD_FIRST_NAME";
    private static final String CC_HEAD_EMAIL = "cc_head@localhost.testing";
    private static final String CC_HEAD_IMAGE_URL = "CC_HEAD_IMAGE_URL";

    private static final String CC_ADMIN_FIRST_NAME = "CC_ADMIN_FIRST_NAME";
    private static final String CC_ADMIN_EMAIL = "cc_admin@localhost.testing";
    private static final String CC_ADMIN_IMAGE_URL = "CC_ADMIN_IMAGE_URL";

    private static final String EVENT_HEAD_FIRST_NAME = "EVENT_HEAD_FIRST_NAME";
    private static final String EVENT_HEAD_EMAIL = "event_head@localhost.testing";
    private static final String EVENT_HEAD_IMAGE_URL = "EVENT_HEAD_IMAGE_URL";

    private static final String EVENT_CREW_FIRST_NAME = "EVENT_CREW_FIRST_NAME";
    private static final String EVENT_CREW_EMAIL = "event_crew@localhost.testing";
    private static final String EVENT_CREW_IMAGE_URL = "EVENT_CREW_IMAGE_URL";

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long DEFAULT_COURSE_PROGRAM_ID = 1L;
    private static final Long UPDATED_COURSE_PROGRAM_ID = 1L;
    private static final String DEFAULT_FIRST_NAME = "DEFAULT_FIRST_NAME";
    private static final String UPDATED_FIRST_NAME = "UPDATED_FIRST_NAME";
    private static final String DEFAULT_LAST_NAME = "DEFAULT_FIRST_NAME";
    private static final String UPDATED_LAST_NAME = "UPDATED_LAST_NAME";
    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;
    private static final String DEFAULT_PHONE_NUMBER = "DEFAULT_PHONE_NUMBER";
    private static final String UPDATED_PHONE_NUMBER = "UPDATED_PHONE_NUMBER";
    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.ofEpochDay(1232L);
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.ofEpochDay(4132L);
    private static final String DEFAULT_YEAR_SESSION = "DEFAULT_YEAR_SESSION";
    private static final String UPDATED_YEAR_SESSION = "UPDATED_YEAR_SESSION";
    private static final Integer DEFAULT_INTAKE_SEMESTER = 1;
    private static final Integer UPDATED_INTAKE_SEMESTER = 1;
    private static final String DEFAULT_STAY_IN = "DEFAULT_STAY_IN";
    private static final String UPDATED_STAY_IN = "UPDATED_STAY_IN";
    private static final UserUniStatus DEFAULT_USER_UNI_STATUS = UserUniStatus.EXTENDED;
    private static final UserUniStatus UPDATED_USER_UNI_STATUS = UserUniStatus.GRADUATED;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private EventCrewRepository eventCrewRepository;

    @Autowired
    private UserUniInfoRepository userUniInfoRepository;

    @Autowired
    private CourseProgramRepository courseProgramRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService mockUserService;

    @Mock
    private MailService mockMailService;

    @Autowired
    private MockMvc restMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMailService).sendActivationEmail(any());
    }

    @AfterEach
    public void cleanUp() {
        userUniInfoRepository.deleteAll();
    }

    private UserUniInfoDTO createDefaultUserUniInfoDTO() {
        UserUniInfoDTO dto = new UserUniInfoDTO();
        dto.setFirstName(DEFAULT_FIRST_NAME);
        dto.setLastName(DEFAULT_LAST_NAME);
        dto.setGender(DEFAULT_GENDER);
        dto.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        dto.setDateOfBirth(DEFAULT_DATE_OF_BIRTH);
        dto.setUserId(DEFAULT_USER_ID);
        dto.setCourseProgramId(DEFAULT_COURSE_PROGRAM_ID);
        dto.setYearSession(DEFAULT_YEAR_SESSION);
        dto.setIntakeSemester(DEFAULT_INTAKE_SEMESTER);
        dto.setStayIn(DEFAULT_STAY_IN);
        dto.setStatus(DEFAULT_USER_UNI_STATUS);
        return dto;
    }

    private UserUniInfoDTO createUpdateUserUniInfoDTO() {
        UserUniInfoDTO dto = new UserUniInfoDTO();
        dto.setFirstName(UPDATED_FIRST_NAME);
        dto.setLastName(UPDATED_LAST_NAME);
        dto.setGender(UPDATED_GENDER);
        dto.setPhoneNumber(UPDATED_PHONE_NUMBER);
        dto.setDateOfBirth(UPDATED_DATE_OF_BIRTH);
        dto.setUserId(Long.MAX_VALUE); // should not be able to update userId
        dto.setCourseProgramId(UPDATED_COURSE_PROGRAM_ID);
        dto.setYearSession(UPDATED_YEAR_SESSION);
        dto.setIntakeSemester(UPDATED_INTAKE_SEMESTER);
        dto.setStayIn(UPDATED_STAY_IN);
        dto.setStatus(UPDATED_USER_UNI_STATUS);
        return dto;
    }

    @Test
    @WithUnauthenticatedMockUser
    public void testNonAuthenticatedUser() throws Exception {
        restMvc.perform(get("/api/authenticate")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        restMvc.perform(get("/api/authenticate")
            .with(request -> {
                request.setRemoteUser(TEST_USER_LOGIN);
                return request;
            })
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(TEST_USER_LOGIN));
    }

    @Test
    @WithNormalUser(firstName = NORMAL_FIRST_NAME, email = NORMAL_EMAIL_1, imageUrl = NORMAL_IMAGE_URL)
    public void getAccountDetails_WithProfileCompleted() throws Exception {
        CourseProgram courseProgram = courseProgramRepository
            .findById(1L)
            .orElseThrow(() -> new RuntimeException("CourseProgram not loaded via liquibase testFaker context"));

        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        currentUser.setFirstName(NORMAL_FIRST_NAME);
        currentUser.setLastName(DEFAULT_LAST_NAME);
        currentUser.setGender(DEFAULT_GENDER);
        currentUser.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        currentUser.setDateOfBirth(DEFAULT_DATE_OF_BIRTH);
        userRepository.saveAndFlush(currentUser);

        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setCourseProgramId(courseProgram.getId());
        userUniInfo.setYearSession(DEFAULT_YEAR_SESSION);
        userUniInfo.setIntakeSemester(DEFAULT_INTAKE_SEMESTER);
        userUniInfo.setStayIn(DEFAULT_STAY_IN);
        userUniInfo.setStatus(DEFAULT_USER_UNI_STATUS);
        userUniInfoRepository.saveAndFlush(userUniInfo);

        restMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(currentUser.getId()))
            .andExpect(jsonPath("$.firstName").value(NORMAL_FIRST_NAME))
            .andExpect(jsonPath("$.email").value(NORMAL_EMAIL_1))
            .andExpect(jsonPath("$.imageUrl").value(NORMAL_IMAGE_URL))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItems(AuthoritiesConstants.USER)))
            .andExpect(jsonPath("$.isProfileCompleted").value(Boolean.TRUE))
            .andExpect(jsonPath("$.isCurrentCCHead").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentAdministrator").value(Boolean.FALSE))
            .andExpect(jsonPath("$.eventHeadEventIds").isEmpty())
            .andExpect(jsonPath("$.eventCrewEventIds").isEmpty());
    }

    @Test
    @WithNormalUser(firstName = NORMAL_FIRST_NAME, email = NORMAL_EMAIL_2, imageUrl = NORMAL_IMAGE_URL)
    public void getAccountDetails_WithBasicProfileIncomplete_ShouldReturnFalse() throws Exception {
        CourseProgram courseProgram = courseProgramRepository
            .findById(1L)
            .orElseThrow(() -> new RuntimeException("CourseProgram not loaded via liquibase testFaker context"));

        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setCourseProgramId(courseProgram.getId());
        userUniInfo.setYearSession(DEFAULT_YEAR_SESSION);
        userUniInfo.setIntakeSemester(DEFAULT_INTAKE_SEMESTER);
        userUniInfo.setStayIn(DEFAULT_STAY_IN);
        userUniInfo.setStatus(DEFAULT_USER_UNI_STATUS);
        userUniInfoRepository.saveAndFlush(userUniInfo);

        restMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(currentUser.getId()))
            .andExpect(jsonPath("$.firstName").value(NORMAL_FIRST_NAME))
            .andExpect(jsonPath("$.email").value(NORMAL_EMAIL_2))
            .andExpect(jsonPath("$.imageUrl").value(NORMAL_IMAGE_URL))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItems(AuthoritiesConstants.USER)))
            .andExpect(jsonPath("$.isProfileCompleted").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentCCHead").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentAdministrator").value(Boolean.FALSE))
            .andExpect(jsonPath("$.eventHeadEventIds").isEmpty())
            .andExpect(jsonPath("$.eventCrewEventIds").isEmpty());
    }

    @Test
    @WithNormalUser(firstName = NORMAL_FIRST_NAME, email = NORMAL_EMAIL_3, imageUrl = NORMAL_IMAGE_URL)
    public void getAccountDetails_WithBasicUserUniInfoIncomplete_ShouldReturnFalse() throws Exception {
        CourseProgram courseProgram = courseProgramRepository
            .findById(1L)
            .orElseThrow(() -> new RuntimeException("CourseProgram not loaded via liquibase testFaker context"));

        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        currentUser.setFirstName(NORMAL_FIRST_NAME);
        currentUser.setLastName(DEFAULT_LAST_NAME);
        currentUser.setGender(DEFAULT_GENDER);
        currentUser.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        currentUser.setDateOfBirth(DEFAULT_DATE_OF_BIRTH);
        userRepository.saveAndFlush(currentUser);

        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setCourseProgramId(courseProgram.getId());
        userUniInfo.setYearSession("");
        userUniInfo.setIntakeSemester(DEFAULT_INTAKE_SEMESTER);
        userUniInfo.setStayIn(DEFAULT_STAY_IN);
        userUniInfo.setStatus(DEFAULT_USER_UNI_STATUS);
        userUniInfoRepository.saveAndFlush(userUniInfo);

        restMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(currentUser.getId()))
            .andExpect(jsonPath("$.firstName").value(NORMAL_FIRST_NAME))
            .andExpect(jsonPath("$.email").value(NORMAL_EMAIL_3))
            .andExpect(jsonPath("$.imageUrl").value(NORMAL_IMAGE_URL))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItems(AuthoritiesConstants.USER)))
            .andExpect(jsonPath("$.isProfileCompleted").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentCCHead").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentAdministrator").value(Boolean.FALSE))
            .andExpect(jsonPath("$.eventHeadEventIds").isEmpty())
            .andExpect(jsonPath("$.eventCrewEventIds").isEmpty());
    }

    @Test
    @WithCurrentCCHead(firstName = CC_HEAD_FIRST_NAME, email = CC_HEAD_EMAIL, imageUrl = CC_HEAD_IMAGE_URL)
    public void getAccountDetails_WithCurrentCCHead() throws Exception {
        User currentUser = userService
            .getUserWithAuthorities()
            .orElseThrow(() -> new RuntimeException("Cannot get current user"));
        restMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(currentUser.getId()))
            .andExpect(jsonPath("$.firstName").value(CC_HEAD_FIRST_NAME))
            .andExpect(jsonPath("$.email").value(CC_HEAD_EMAIL))
            .andExpect(jsonPath("$.imageUrl").value(CC_HEAD_IMAGE_URL))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItems(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)))
            .andExpect(jsonPath("$.isProfileCompleted").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentCCHead").value(Boolean.TRUE))
            .andExpect(jsonPath("$.isCurrentAdministrator").value(Boolean.TRUE))
            .andExpect(jsonPath("$.eventHeadEventIds").isEmpty())
            .andExpect(jsonPath("$.eventCrewEventIds").isEmpty());
    }

    @Test
    @WithCurrentCCAdministrator(firstName = CC_ADMIN_FIRST_NAME, email = CC_ADMIN_EMAIL, imageUrl = CC_ADMIN_IMAGE_URL)
    public void getAccountDetails_WithCurrentCCAdministrator() throws Exception {
        User currentUser = userService
            .getUserWithAuthorities()
            .orElseThrow(() -> new RuntimeException("Cannot get current user"));
        restMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(currentUser.getId()))
            .andExpect(jsonPath("$.firstName").value(CC_ADMIN_FIRST_NAME))
            .andExpect(jsonPath("$.email").value(CC_ADMIN_EMAIL))
            .andExpect(jsonPath("$.imageUrl").value(CC_ADMIN_IMAGE_URL))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItems(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)))
            .andExpect(jsonPath("$.isProfileCompleted").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentCCHead").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentAdministrator").value(Boolean.TRUE))
            .andExpect(jsonPath("$.eventHeadEventIds").isEmpty())
            .andExpect(jsonPath("$.eventCrewEventIds").isEmpty());
    }

    @Test
    @WithEventHead(firstName = EVENT_HEAD_FIRST_NAME, email = EVENT_HEAD_EMAIL, imageUrl = EVENT_HEAD_IMAGE_URL)
    public void getAccountDetails_WithEventHead() throws Exception {
        User currentUser = userService
            .getUserWithAuthorities()
            .orElseThrow(() -> new RuntimeException("Cannot get current user"));
        EventCrew eventCrew = getEventCrewByCurrentLoginUser();
        restMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(currentUser.getId()))
            .andExpect(jsonPath("$.firstName").value(EVENT_HEAD_FIRST_NAME))
            .andExpect(jsonPath("$.email").value(EVENT_HEAD_EMAIL))
            .andExpect(jsonPath("$.imageUrl").value(EVENT_HEAD_IMAGE_URL))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItems(AuthoritiesConstants.USER)))
            .andExpect(jsonPath("$.isProfileCompleted").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentCCHead").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentAdministrator").value(Boolean.FALSE))
            .andExpect(jsonPath("$.eventHeadEventIds").value(Matchers.hasItem(eventCrew.getEventId().intValue())))
            .andExpect(jsonPath("$.eventHeadEventIds").value(Matchers.hasSize(1)))
            .andExpect(jsonPath("$.eventCrewEventIds").value(Matchers.hasItem(eventCrew.getEventId().intValue())))
            .andExpect(jsonPath("$.eventCrewEventIds").value(Matchers.hasSize(1)));
    }

    @Test
    @WithEventCrew(firstName = EVENT_CREW_FIRST_NAME, email = EVENT_CREW_EMAIL, imageUrl = EVENT_CREW_IMAGE_URL)
    public void getAccountDetails_WithEventCrew() throws Exception {
        User currentUser = userService
            .getUserWithAuthorities()
            .orElseThrow(() -> new RuntimeException("Cannot get current user"));
        EventCrew eventCrew = getEventCrewByCurrentLoginUser();
        restMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(currentUser.getId()))
            .andExpect(jsonPath("$.firstName").value(EVENT_CREW_FIRST_NAME))
            .andExpect(jsonPath("$.email").value(EVENT_CREW_EMAIL))
            .andExpect(jsonPath("$.imageUrl").value(EVENT_CREW_IMAGE_URL))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItems(AuthoritiesConstants.USER)))
            .andExpect(jsonPath("$.isProfileCompleted").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentCCHead").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentAdministrator").value(Boolean.FALSE))
            .andExpect(jsonPath("$.eventHeadEventIds").isEmpty())
            .andExpect(jsonPath("$.eventCrewEventIds").value(Matchers.hasItem(eventCrew.getEventId().intValue())))
            .andExpect(jsonPath("$.eventCrewEventIds").value(Matchers.hasSize(1)));
    }

    @Test
    @WithMockUser
    public void getAccountDetails_WithNormalUser() throws Exception {
        User currentUser = userService
            .getUserWithAuthorities()
            .orElseThrow(() -> new RuntimeException("Cannot get current user"));
        restMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(currentUser.getId()))
            .andExpect(jsonPath("$.firstName").value("User"))
            .andExpect(jsonPath("$.email").value("user@localhost"))
            .andExpect(jsonPath("$.imageUrl").value(""))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItems(AuthoritiesConstants.USER)))
            .andExpect(jsonPath("$.isProfileCompleted").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentCCHead").value(Boolean.FALSE))
            .andExpect(jsonPath("$.isCurrentAdministrator").value(Boolean.FALSE))
            .andExpect(jsonPath("$.eventHeadEventIds").isEmpty())
            .andExpect(jsonPath("$.eventCrewEventIds").isEmpty());
    }

    @Test
    @WithUnauthenticatedMockUser
    public void getAccountDetails_WithUnauthenticatedUser_ShouldThrow500() throws Exception {
        restMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    public void testRegisterValid() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM();
        validUser.setLogin("test-register-valid");
        validUser.setPassword("password");
        validUser.setFirstName("Alice");
        validUser.setLastName("Test");
        validUser.setEmail("test-register-valid@example.com");
        validUser.setImageUrl("http://placehold.it/50x50");
        validUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        validUser.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
        assertThat(userRepository.findOneByLogin("test-register-valid").isPresent()).isFalse();

        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(validUser)))
            .andExpect(status().isCreated());

        assertThat(userRepository.findOneByLogin("test-register-valid").isPresent()).isTrue();
    }

    @Test
    @Transactional
    public void testRegisterInvalidLogin() throws Exception {
        ManagedUserVM invalidUser = new ManagedUserVM();
        invalidUser.setLogin("funky-log!n");// <-- invalid
        invalidUser.setPassword("password");
        invalidUser.setFirstName("Funky");
        invalidUser.setLastName("One");
        invalidUser.setEmail("funky@example.com");
        invalidUser.setActivated(true);
        invalidUser.setImageUrl("http://placehold.it/50x50");
        invalidUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        invalidUser.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByEmailIgnoreCase("funky@example.com");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterInvalidEmail() throws Exception {
        ManagedUserVM invalidUser = new ManagedUserVM();
        invalidUser.setLogin("bob");
        invalidUser.setPassword("password");
        invalidUser.setFirstName("Bob");
        invalidUser.setLastName("Green");
        invalidUser.setEmail("invalid");// <-- invalid
        invalidUser.setActivated(true);
        invalidUser.setImageUrl("http://placehold.it/50x50");
        invalidUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        invalidUser.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterInvalidPassword() throws Exception {
        ManagedUserVM invalidUser = new ManagedUserVM();
        invalidUser.setLogin("bob");
        invalidUser.setPassword("123");// password with only 3 digits
        invalidUser.setFirstName("Bob");
        invalidUser.setLastName("Green");
        invalidUser.setEmail("bob@example.com");
        invalidUser.setActivated(true);
        invalidUser.setImageUrl("http://placehold.it/50x50");
        invalidUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        invalidUser.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterNullPassword() throws Exception {
        ManagedUserVM invalidUser = new ManagedUserVM();
        invalidUser.setLogin("bob");
        invalidUser.setPassword(null);// invalid null password
        invalidUser.setFirstName("Bob");
        invalidUser.setLastName("Green");
        invalidUser.setEmail("bob@example.com");
        invalidUser.setActivated(true);
        invalidUser.setImageUrl("http://placehold.it/50x50");
        invalidUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        invalidUser.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterDuplicateLogin() throws Exception {
        // First registration
        ManagedUserVM firstUser = new ManagedUserVM();
        firstUser.setLogin("alice");
        firstUser.setPassword("password");
        firstUser.setFirstName("Alice");
        firstUser.setLastName("Something");
        firstUser.setEmail("alice@example.com");
        firstUser.setImageUrl("http://placehold.it/50x50");
        firstUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        firstUser.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        // Duplicate login, different email
        ManagedUserVM secondUser = new ManagedUserVM();
        secondUser.setLogin(firstUser.getLogin());
        secondUser.setPassword(firstUser.getPassword());
        secondUser.setFirstName(firstUser.getFirstName());
        secondUser.setLastName(firstUser.getLastName());
        secondUser.setEmail("alice2@example.com");
        secondUser.setImageUrl(firstUser.getImageUrl());
        secondUser.setLangKey(firstUser.getLangKey());
        secondUser.setCreatedBy(firstUser.getCreatedBy());
        secondUser.setCreatedDate(firstUser.getCreatedDate());
        secondUser.setLastModifiedBy(firstUser.getLastModifiedBy());
        secondUser.setLastModifiedDate(firstUser.getLastModifiedDate());
        secondUser.setAuthorities(new HashSet<>(firstUser.getAuthorities()));

        // First user
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(firstUser)))
            .andExpect(status().isCreated());

        // Second (non activated) user
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(secondUser)))
            .andExpect(status().isCreated());

        Optional<User> testUser = userRepository.findOneByEmailIgnoreCase("alice2@example.com");
        assertThat(testUser.isPresent()).isTrue();
        testUser.get().setActivated(true);
        userRepository.save(testUser.get());

        // Second (already activated) user
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(secondUser)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    public void testRegisterDuplicateEmail() throws Exception {
        // First user
        ManagedUserVM firstUser = new ManagedUserVM();
        firstUser.setLogin("test-register-duplicate-email");
        firstUser.setPassword("password");
        firstUser.setFirstName("Alice");
        firstUser.setLastName("Test");
        firstUser.setEmail("test-register-duplicate-email@example.com");
        firstUser.setImageUrl("http://placehold.it/50x50");
        firstUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        firstUser.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        // Register first user
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(firstUser)))
            .andExpect(status().isCreated());

        Optional<User> testUser1 = userRepository.findOneByLogin("test-register-duplicate-email");
        assertThat(testUser1.isPresent()).isTrue();

        // Duplicate email, different login
        ManagedUserVM secondUser = new ManagedUserVM();
        secondUser.setLogin("test-register-duplicate-email-2");
        secondUser.setPassword(firstUser.getPassword());
        secondUser.setFirstName(firstUser.getFirstName());
        secondUser.setLastName(firstUser.getLastName());
        secondUser.setEmail(firstUser.getEmail());
        secondUser.setImageUrl(firstUser.getImageUrl());
        secondUser.setLangKey(firstUser.getLangKey());
        secondUser.setAuthorities(new HashSet<>(firstUser.getAuthorities()));

        // Register second (non activated) user
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(secondUser)))
            .andExpect(status().isCreated());

        Optional<User> testUser2 = userRepository.findOneByLogin("test-register-duplicate-email");
        assertThat(testUser2.isPresent()).isFalse();

        Optional<User> testUser3 = userRepository.findOneByLogin("test-register-duplicate-email-2");
        assertThat(testUser3.isPresent()).isTrue();

        // Duplicate email - with uppercase email address
        ManagedUserVM userWithUpperCaseEmail = new ManagedUserVM();
        userWithUpperCaseEmail.setId(firstUser.getId());
        userWithUpperCaseEmail.setLogin("test-register-duplicate-email-3");
        userWithUpperCaseEmail.setPassword(firstUser.getPassword());
        userWithUpperCaseEmail.setFirstName(firstUser.getFirstName());
        userWithUpperCaseEmail.setLastName(firstUser.getLastName());
        userWithUpperCaseEmail.setEmail("TEST-register-duplicate-email@example.com");
        userWithUpperCaseEmail.setImageUrl(firstUser.getImageUrl());
        userWithUpperCaseEmail.setLangKey(firstUser.getLangKey());
        userWithUpperCaseEmail.setAuthorities(new HashSet<>(firstUser.getAuthorities()));

        // Register third (not activated) user
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userWithUpperCaseEmail)))
            .andExpect(status().isCreated());

        Optional<User> testUser4 = userRepository.findOneByLogin("test-register-duplicate-email-3");
        assertThat(testUser4.isPresent()).isTrue();
        assertThat(testUser4.get().getEmail()).isEqualTo("test-register-duplicate-email@example.com");

        testUser4.get().setActivated(true);
        userService.updateUser((new UserDTO(testUser4.get())));

        // Register 4th (already activated) user
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(secondUser)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    public void testRegisterAdminIsIgnored() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM();
        validUser.setLogin("badguy");
        validUser.setPassword("password");
        validUser.setFirstName("Bad");
        validUser.setLastName("Guy");
        validUser.setEmail("badguy@example.com");
        validUser.setActivated(true);
        validUser.setImageUrl("http://placehold.it/50x50");
        validUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        validUser.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(validUser)))
            .andExpect(status().isCreated());

        Optional<User> userDup = userRepository.findOneByLogin("badguy");
        assertThat(userDup.isPresent()).isTrue();
        assertThat(userDup.get().getAuthorities()).hasSize(1)
            .containsExactly(authorityRepository.findById(AuthoritiesConstants.USER).get());
    }

    @Test
    @Transactional
    public void testActivateAccount() throws Exception {
        final String activationKey = "some activation key";
        User user = new User();
        user.setLogin("activate-account");
        user.setEmail("activate-account@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(false);
        user.setActivationKey(activationKey);

        userRepository.saveAndFlush(user);

        restMvc.perform(get("/api/activate?key={activationKey}", activationKey))
            .andExpect(status().isOk());

        user = userRepository.findOneByLogin(user.getLogin()).orElse(null);
        assertThat(user.isActivated()).isTrue();
    }

    @Test
    @Transactional
    public void testActivateAccountWithWrongKey() throws Exception {
        restMvc.perform(get("/api/activate?key=wrongActivationKey"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    @WithMockUser("save-account")
    public void testSaveAccount() throws Exception {
        User user = new User();
        user.setLogin("save-account");
        user.setEmail("save-account@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);

        userRepository.saveAndFlush(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("not-used");
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("save-account@example.com");
        userDTO.setActivated(false);
        userDTO.setImageUrl("http://placehold.it/50x50");
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        restMvc.perform(
            post("/api/account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByLogin(user.getLogin()).orElse(null);
        assertThat(updatedUser.getFirstName()).isEqualTo(userDTO.getFirstName());
        assertThat(updatedUser.getLastName()).isEqualTo(userDTO.getLastName());
        assertThat(updatedUser.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(updatedUser.getLangKey()).isEqualTo(userDTO.getLangKey());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.getImageUrl()).isEqualTo(userDTO.getImageUrl());
        assertThat(updatedUser.isActivated()).isEqualTo(true);
        assertThat(updatedUser.getAuthorities()).isEmpty();
    }

    @Test
    @Transactional
    @WithMockUser("save-invalid-email")
    public void testSaveInvalidEmail() throws Exception {
        User user = new User();
        user.setLogin("save-invalid-email");
        user.setEmail("save-invalid-email@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);

        userRepository.saveAndFlush(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("not-used");
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("invalid email");
        userDTO.setActivated(false);
        userDTO.setImageUrl("http://placehold.it/50x50");
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        restMvc.perform(
            post("/api/account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isBadRequest());

        assertThat(userRepository.findOneByEmailIgnoreCase("invalid email")).isNotPresent();
    }

    @Test
    @Transactional
    @WithMockUser("save-existing-email")
    public void testSaveExistingEmail() throws Exception {
        User user = new User();
        user.setLogin("save-existing-email");
        user.setEmail("save-existing-email@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);

        userRepository.saveAndFlush(user);

        User anotherUser = new User();
        anotherUser.setLogin("save-existing-email2");
        anotherUser.setEmail("save-existing-email2@example.com");
        anotherUser.setPassword(RandomStringUtils.random(60));
        anotherUser.setActivated(true);

        userRepository.saveAndFlush(anotherUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("not-used");
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("save-existing-email2@example.com");
        userDTO.setActivated(false);
        userDTO.setImageUrl("http://placehold.it/50x50");
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        restMvc.perform(
            post("/api/account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("save-existing-email").orElse(null);
        assertThat(updatedUser.getEmail()).isEqualTo("save-existing-email@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("save-existing-email-and-login")
    public void testSaveExistingEmailAndLogin() throws Exception {
        User user = new User();
        user.setLogin("save-existing-email-and-login");
        user.setEmail("save-existing-email-and-login@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);

        userRepository.saveAndFlush(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("not-used");
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("save-existing-email-and-login@example.com");
        userDTO.setActivated(false);
        userDTO.setImageUrl("http://placehold.it/50x50");
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        restMvc.perform(
            post("/api/account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByLogin("save-existing-email-and-login").orElse(null);
        assertThat(updatedUser.getEmail()).isEqualTo("save-existing-email-and-login@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("change-password-wrong-existing-password")
    public void testChangePasswordWrongExistingPassword() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("change-password-wrong-existing-password");
        user.setEmail("change-password-wrong-existing-password@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/change-password")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO("1"+currentPassword, "new password"))))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change-password-wrong-existing-password").orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    @WithMockUser("change-password")
    public void testChangePassword() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("change-password");
        user.setEmail("change-password@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/change-password")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, "new password"))))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByLogin("change-password").orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    @WithMockUser("change-password-too-small")
    public void testChangePasswordTooSmall() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("change-password-too-small");
        user.setEmail("change-password-too-small@example.com");
        userRepository.saveAndFlush(user);

        String newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MIN_LENGTH - 1);

        restMvc.perform(post("/api/account/change-password")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, newPassword))))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change-password-too-small").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    @WithMockUser("change-password-too-long")
    public void testChangePasswordTooLong() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("change-password-too-long");
        user.setEmail("change-password-too-long@example.com");
        userRepository.saveAndFlush(user);

        String newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MAX_LENGTH + 1);

        restMvc.perform(post("/api/account/change-password")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, newPassword))))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change-password-too-long").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    @WithMockUser("change-password-empty")
    public void testChangePasswordEmpty() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("change-password-empty");
        user.setEmail("change-password-empty@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/change-password")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, ""))))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change-password-empty").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    public void testRequestPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setLogin("password-reset");
        user.setEmail("password-reset@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/reset-password/init")
            .content("password-reset@example.com"))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testRequestPasswordResetUpperCaseEmail() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setLogin("password-reset");
        user.setEmail("password-reset@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/reset-password/init")
            .content("password-reset@EXAMPLE.COM"))
            .andExpect(status().isOk());
    }

    @Test
    public void testRequestPasswordResetWrongEmail() throws Exception {
        restMvc.perform(
            post("/api/account/reset-password/init")
                .content("password-reset-wrong-email@example.com"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testFinishPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setLogin("finish-password-reset");
        user.setEmail("finish-password-reset@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key");
        userRepository.saveAndFlush(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("new password");

        restMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByLogin(user.getLogin()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    public void testFinishPasswordResetTooSmall() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setLogin("finish-password-reset-too-small");
        user.setEmail("finish-password-reset-too-small@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key too small");
        userRepository.saveAndFlush(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("foo");

        restMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin(user.getLogin()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isFalse();
    }


    @Test
    @Transactional
    public void testFinishPasswordResetWrongKey() throws Exception {
        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey("wrong reset key");
        keyAndPassword.setNewPassword("new password");

        restMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isInternalServerError());
    }


    @Test
    @WithNormalUser
    @Deprecated
    public void isProfileCompleted_WithProfileCompleted() throws Exception {
        CourseProgram courseProgram = courseProgramRepository
            .findById(1L)
            .orElseThrow(() -> new RuntimeException("CourseProgram not loaded via liquibase testFaker context"));

        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        currentUser.setFirstName(DEFAULT_FIRST_NAME);
        currentUser.setLastName(DEFAULT_LAST_NAME);
        currentUser.setGender(DEFAULT_GENDER);
        currentUser.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        currentUser.setDateOfBirth(DEFAULT_DATE_OF_BIRTH);
        userRepository.saveAndFlush(currentUser);

        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setCourseProgramId(courseProgram.getId());
        userUniInfo.setYearSession(DEFAULT_YEAR_SESSION);
        userUniInfo.setIntakeSemester(DEFAULT_INTAKE_SEMESTER);
        userUniInfo.setStayIn(DEFAULT_STAY_IN);
        userUniInfo.setStatus(DEFAULT_USER_UNI_STATUS);
        userUniInfoRepository.saveAndFlush(userUniInfo);

        restMvc.perform(get("/api/account/is-profile-completed"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.isProfileCompleted").value(true));
    }

    @Test
    @WithNormalUser
    @Deprecated
    public void isProfileCompleted_WithBasicProfileIncomplete_ShouldReturnFalse() throws Exception {
        CourseProgram courseProgram = courseProgramRepository
            .findById(1L)
            .orElseThrow(() -> new RuntimeException("CourseProgram not loaded via liquibase testFaker context"));

        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        currentUser.setFirstName("");
        currentUser.setLastName(DEFAULT_LAST_NAME);
        currentUser.setGender(DEFAULT_GENDER);
        currentUser.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        currentUser.setDateOfBirth(DEFAULT_DATE_OF_BIRTH);
        userRepository.saveAndFlush(currentUser);

        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setCourseProgramId(courseProgram.getId());
        userUniInfo.setYearSession(DEFAULT_YEAR_SESSION);
        userUniInfo.setIntakeSemester(DEFAULT_INTAKE_SEMESTER);
        userUniInfo.setStayIn(DEFAULT_STAY_IN);
        userUniInfo.setStatus(DEFAULT_USER_UNI_STATUS);
        userUniInfoRepository.saveAndFlush(userUniInfo);

        restMvc.perform(get("/api/account/is-profile-completed"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.isProfileCompleted").value(false));
    }

    @Test
    @WithNormalUser
    @Deprecated
    public void isProfileCompleted_WithBasicUserUniInfoIncomplete_ShouldReturnFalse() throws Exception {
        CourseProgram courseProgram = courseProgramRepository
            .findById(1L)
            .orElseThrow(() -> new RuntimeException("CourseProgram not loaded via liquibase testFaker context"));

        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        currentUser.setFirstName(DEFAULT_FIRST_NAME);
        currentUser.setLastName(DEFAULT_LAST_NAME);
        currentUser.setGender(DEFAULT_GENDER);
        currentUser.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        currentUser.setDateOfBirth(DEFAULT_DATE_OF_BIRTH);
        userRepository.saveAndFlush(currentUser);

        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setUserId(currentUser.getId());
        userUniInfo.setCourseProgramId(courseProgram.getId());
        userUniInfo.setYearSession("");
        userUniInfo.setIntakeSemester(DEFAULT_INTAKE_SEMESTER);
        userUniInfo.setStayIn(DEFAULT_STAY_IN);
        userUniInfo.setStatus(DEFAULT_USER_UNI_STATUS);
        userUniInfoRepository.saveAndFlush(userUniInfo);

        restMvc.perform(get("/api/account/is-profile-completed"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.isProfileCompleted").value(false));
    }

    @Test
    @WithNormalUser
    public void completeProfile()  throws Exception {

        CourseProgram courseProgram = courseProgramRepository
            .findById(1L)
            .orElseThrow(() -> new RuntimeException("CourseProgram not loaded via liquibase testFaker context"));

        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        int userUniInfoDBSizeB4Create = userUniInfoRepository.findAll().size();
        int userDBSizeB4Create = userRepository.findAll().size();

        UserUniInfoDTO userUniInfoDTO = createDefaultUserUniInfoDTO();

        restMvc.perform(post("/api/account/profile")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userUniInfoDTO)))
            .andExpect(status().isOk());

        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(userDBSizeB4Create);
        User user = userRepository
            .findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("Cannot find user"));
        assertThat(user.getId()).isEqualTo(currentUser.getId());
        assertThat(user.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(user.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(user.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(user.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(user.getDateOfBirth()).isEqualTo(DEFAULT_DATE_OF_BIRTH);

        List<UserUniInfo> userUniInfoList = userUniInfoRepository.findAll();
        assertThat(userUniInfoList).hasSize(userUniInfoDBSizeB4Create + 1);
        UserUniInfo testUserUniInfo = userUniInfoList.get(userUniInfoList.size() - 1);
        assertThat(testUserUniInfo.getUserId()).isEqualTo(currentUser.getId());
        assertThat(testUserUniInfo.getCourseProgramId()).isEqualTo(courseProgram.getId());
        assertThat(testUserUniInfo.getYearSession()).isEqualTo(DEFAULT_YEAR_SESSION);
        assertThat(testUserUniInfo.getIntakeSemester()).isEqualTo(DEFAULT_INTAKE_SEMESTER);
        assertThat(testUserUniInfo.getStayIn()).isEqualTo(DEFAULT_STAY_IN);
        assertThat(testUserUniInfo.getStatus()).isEqualTo(DEFAULT_USER_UNI_STATUS);
    }

    @Test
    @WithNormalUser
    public void completeProfile_WithUserUniStatusIsNull_ShouldDefaultItAsStudying()  throws Exception {

        CourseProgram courseProgram = courseProgramRepository
            .findById(1L)
            .orElseThrow(() -> new RuntimeException("CourseProgram not loaded via liquibase testFaker context"));

        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        int userUniInfoDBSizeB4Create = userUniInfoRepository.findAll().size();
        int userDBSizeB4Create = userRepository.findAll().size();

        UserUniInfoDTO userUniInfoDTO = createDefaultUserUniInfoDTO();
        userUniInfoDTO.setStatus(null);

        assertThat(userUniInfoDTO.getStatus()).isNull();

        restMvc.perform(post("/api/account/profile")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userUniInfoDTO)))
            .andExpect(status().isOk());

        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(userDBSizeB4Create);
        User user = userRepository
            .findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("Cannot find user"));
        assertThat(user.getId()).isEqualTo(currentUser.getId());
        assertThat(user.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(user.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(user.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(user.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(user.getDateOfBirth()).isEqualTo(DEFAULT_DATE_OF_BIRTH);

        List<UserUniInfo> userUniInfoList = userUniInfoRepository.findAll();
        assertThat(userUniInfoList).hasSize(userUniInfoDBSizeB4Create + 1);
        UserUniInfo testUserUniInfo = userUniInfoList.get(userUniInfoList.size() - 1);
        assertThat(testUserUniInfo.getUserId()).isEqualTo(currentUser.getId());
        assertThat(testUserUniInfo.getCourseProgramId()).isEqualTo(courseProgram.getId());
        assertThat(testUserUniInfo.getYearSession()).isEqualTo(DEFAULT_YEAR_SESSION);
        assertThat(testUserUniInfo.getIntakeSemester()).isEqualTo(DEFAULT_INTAKE_SEMESTER);
        assertThat(testUserUniInfo.getStayIn()).isEqualTo(DEFAULT_STAY_IN);
        assertThat(testUserUniInfo.getStatus()).isEqualTo(UserUniStatus.STUDYING);
    }

    @Test
    @WithNormalUser
    public void completeProfile_WithUserUniInfoExistInDB()  throws Exception {

        CourseProgram courseProgram = courseProgramRepository
            .findById(1L)
            .orElseThrow(() -> new RuntimeException("CourseProgram not loaded via liquibase testFaker context"));

        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));

        UserUniInfo savedUserUniInfo = initUserUniInfoDB(currentUser);
        int userUniInfoDBSizeB4Create = userUniInfoRepository.findAll().size();
        int userDBSizeB4Create = userRepository.findAll().size();

        UserUniInfoDTO userUniInfoDTO = createUpdateUserUniInfoDTO();

        restMvc.perform(post("/api/account/profile")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userUniInfoDTO)))
            .andExpect(status().isOk());

        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(userDBSizeB4Create);
        User user = userRepository
            .findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("Cannot find user"));
        assertThat(user.getId()).isEqualTo(currentUser.getId());
        assertThat(user.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(user.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(user.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(user.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(user.getDateOfBirth()).isEqualTo(UPDATED_DATE_OF_BIRTH);

        List<UserUniInfo> userUniInfoList = userUniInfoRepository.findAll();
        assertThat(userUniInfoList).hasSize(userUniInfoDBSizeB4Create);
        UserUniInfo testUserUniInfo = userUniInfoList.get(userUniInfoList.size() - 1);
        assertThat(testUserUniInfo.getUserId()).isEqualTo(currentUser.getId());
        assertThat(testUserUniInfo.getCourseProgramId()).isEqualTo(courseProgram.getId());
        assertThat(testUserUniInfo.getYearSession()).isEqualTo(UPDATED_YEAR_SESSION);
        assertThat(testUserUniInfo.getIntakeSemester()).isEqualTo(UPDATED_INTAKE_SEMESTER);
        assertThat(testUserUniInfo.getStayIn()).isEqualTo(UPDATED_STAY_IN);
        assertThat(testUserUniInfo.getStatus()).isEqualTo(UPDATED_USER_UNI_STATUS);
    }

    @Test
    @WithNormalUser
    public void completeProfile_WithInvalidCourseProgram()  throws Exception {
        int userUniInfoDBSizeB4Create = userUniInfoRepository.findAll().size();

        UserUniInfoDTO userUniInfoDTO = createDefaultUserUniInfoDTO();
        userUniInfoDTO.setCourseProgramId(Long.MAX_VALUE);

        restMvc.perform(post("/api/account/profile")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userUniInfoDTO)))
            .andExpect(status().isBadRequest());

        List<UserUniInfo> userUniInfoList = userUniInfoRepository.findAll();
        assertThat(userUniInfoList).hasSize(userUniInfoDBSizeB4Create);
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

    private UserUniInfo initUserUniInfoDB(User user) {
        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setUserId(user.getId());
        return userUniInfoRepository.saveAndFlush(userUniInfo);
    }
}
