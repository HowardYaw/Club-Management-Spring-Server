package com.thirdcc.webapp.authorization;

import com.thirdcc.webapp.ClubmanagementApp;
import com.thirdcc.webapp.domain.Administrator;
import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.YearSession;
import com.thirdcc.webapp.domain.enumeration.AdministratorRole;
import com.thirdcc.webapp.domain.enumeration.AdministratorStatus;
import com.thirdcc.webapp.domain.enumeration.EventCrewRole;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.AdministratorRepository;
import com.thirdcc.webapp.repository.EventCrewRepository;
import com.thirdcc.webapp.repository.YearSessionRepository;
import com.thirdcc.webapp.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ClubmanagementApp.class)
@WithMockUser
public class ManagementTeamSecurityExpressionIT {

    @Autowired
    private ManagementTeamSecurityExpression managementTeamSecurityExpression;

    @Autowired
    private EventCrewRepository eventCrewRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private YearSessionRepository yearSessionRepository;

    @Autowired
    private UserService userService;

    private User currentUser;
    private Administrator administrator;
    private EventCrew eventCrew;
    private YearSession yearSession;

    private static final String DEFAULT_YEAR_SESSION_VALUE = "2021/2022";
    private static final Long DEFAULT_EVENT_ID = 1L;
    private static final Long DEFAULT_USER_ID = 1L;
    private static final AdministratorRole DEFAULT_ADMINISTRATOR_ROLE = AdministratorRole.SECRETARY;
    private static final AdministratorStatus DEFAULT_ADMINISTRATOR_STATUS = AdministratorStatus.ACTIVE;

    private static final String PREVIOUS_YEAR_SESSION_VALUE = "2020/2021";
    private static final Long OTHER_EVENT_ID = 2L;
    private static final EventCrewRole EVENT_HEAD_ROLE = EventCrewRole.HEAD;
    private static final AdministratorRole CC_HEAD_ROLE = AdministratorRole.CC_HEAD;



    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void init() {
        createYearSession();
        createAdministrator();
        createEventCrew();
    }

    private void createYearSession() {
        yearSession = new YearSession()
            .value(DEFAULT_YEAR_SESSION_VALUE);
    }

    private void createEventCrew() {
        eventCrew = new EventCrew()
            .eventId(DEFAULT_EVENT_ID)
            .userId(DEFAULT_USER_ID);
    }

    private void createAdministrator() {
        administrator = new Administrator()
            .role(DEFAULT_ADMINISTRATOR_ROLE)
            .status(DEFAULT_ADMINISTRATOR_STATUS)
            .yearSession(DEFAULT_YEAR_SESSION_VALUE)
            .userId(DEFAULT_USER_ID);
    }

    @AfterEach
    public void cleanUp() {
        yearSessionRepository.deleteAll();
        eventCrewRepository.deleteAll();
        administratorRepository.deleteAll();
    }

    /**
     * Test on isCurrentCCHead
     */
    @Test
    public void isCurrentCCHead_ShouldReturnTrue() {
        currentUser = getLoggedInUser();

        initYearSessionDB();

        administrator.setRole(CC_HEAD_ROLE);
        administrator.setUserId(currentUser.getId());
        initAdministratorDB();

        Boolean result = managementTeamSecurityExpression.isCurrentCCHead();
        assertThat(result).isTrue();
    }

    @Test
    public void isCurrentCCHead_UserIsNotCCHead_ShouldReturnFalse() {
        currentUser = getLoggedInUser();

        initYearSessionDB();

        administrator.setUserId(currentUser.getId());
        initAdministratorDB();

        Boolean result = managementTeamSecurityExpression.isCurrentCCHead();
        assertThat(result).isFalse();
    }

    @Test
    public void isCurrentCCHead_UserIsPreviousCCHead_ShouldReturnFalse() {
        currentUser = getLoggedInUser();

        yearSessionRepository.save(new YearSession().value(PREVIOUS_YEAR_SESSION_VALUE));
        initYearSessionDB();

        administrator.setRole(CC_HEAD_ROLE);
        administrator.setUserId(currentUser.getId());
        administrator.setYearSession(PREVIOUS_YEAR_SESSION_VALUE);
        initAdministratorDB();

        Boolean result = managementTeamSecurityExpression.isCurrentCCHead();
        assertThat(result).isFalse();
    }

    @Test
    public void isCurrentCCHead_UserIsNotActiveCCHead_ShouldReturnFalse() {
        currentUser = getLoggedInUser();

        initYearSessionDB();

        administrator.setRole(CC_HEAD_ROLE);
        administrator.setUserId(currentUser.getId());
        administrator.setStatus(AdministratorStatus.DEACTIVATE);
        initAdministratorDB();

        Boolean result = managementTeamSecurityExpression.isCurrentCCHead();
        assertThat(result).isFalse();
    }

    /**
     * Test on isCurrentAdministrator
     */
    @Test
    public void isCurrentAdministrator_ShouldReturnTrue() {
        currentUser = getLoggedInUser();

        initYearSessionDB();

        administrator.setUserId(currentUser.getId());
        initAdministratorDB();

        Boolean result = managementTeamSecurityExpression.isCurrentAdministrator();
        assertThat(result).isTrue();
    }

    @Test
    public void isCurrentAdministrator_UserIsNotAdministrator_ShouldReturnFalse() {
        currentUser = getLoggedInUser();

        initYearSessionDB();

        Boolean result = managementTeamSecurityExpression.isCurrentAdministrator();
        assertThat(result).isFalse();
    }

    @Test
    public void isCurrentAdministrator_UserIsPreviousAdministrator_ShouldReturnFalse() {
        currentUser = getLoggedInUser();

        yearSessionRepository.save(new YearSession().value(PREVIOUS_YEAR_SESSION_VALUE));
        initYearSessionDB();

        administrator.setUserId(currentUser.getId());
        administrator.setYearSession(PREVIOUS_YEAR_SESSION_VALUE);
        initAdministratorDB();

        Boolean result = managementTeamSecurityExpression.isCurrentAdministrator();
        assertThat(result).isFalse();
    }

    @Test
    public void isCurrentAdministrator_UserIsNotActiveAdministrator_ShouldReturnFalse() {
        currentUser = getLoggedInUser();

        initYearSessionDB();

        administrator.setUserId(currentUser.getId());
        administrator.setStatus(AdministratorStatus.DEACTIVATE);
        initAdministratorDB();

        Boolean result = managementTeamSecurityExpression.isCurrentAdministrator();
        assertThat(result).isFalse();
    }

    /**
     * Test on isEventHead
     */
    @Test
    public void isEventHead_ShouldReturnTrue() {
        currentUser = getLoggedInUser();

        eventCrew.setUserId(currentUser.getId());
        eventCrew.setRole(EVENT_HEAD_ROLE);
        initEventCrewDB();

        Boolean result = managementTeamSecurityExpression.isEventHead(DEFAULT_EVENT_ID);
        assertThat(result).isTrue();
    }

    @Test
    public void isEventHead_UserIsNotEventHead_ShouldReturnFalse() {
        currentUser = getLoggedInUser();

        eventCrew.setUserId(currentUser.getId());
        initEventCrewDB();

        Boolean result = managementTeamSecurityExpression.isEventHead(DEFAULT_EVENT_ID);
        assertThat(result).isFalse();
    }

    @Test
    public void isEventHead_UserIsEventHeadOfAnotherEvent_ShouldReturnFalse() {
        currentUser = getLoggedInUser();

        eventCrew.setUserId(currentUser.getId());
        eventCrew.setRole(EVENT_HEAD_ROLE);
        initEventCrewDB();

        Boolean result = managementTeamSecurityExpression.isEventHead(OTHER_EVENT_ID);
        assertThat(result).isFalse();
    }

    /**
     * Test on isEventCrew
     */
    @Test
    public void isEventCrew_ShouldReturnTrue() {
        currentUser = getLoggedInUser();

        eventCrew.setUserId(currentUser.getId());
        initEventCrewDB();

        Boolean result = managementTeamSecurityExpression.isEventCrew(DEFAULT_EVENT_ID);
        assertThat(result).isTrue();
    }

    @Test
    public void isEventCrew_UserIsNotEventCrew_ShouldReturnFalse() {
        currentUser = getLoggedInUser();

        Boolean result = managementTeamSecurityExpression.isEventCrew(DEFAULT_EVENT_ID);
        assertThat(result).isFalse();
    }

    @Test
    public void isEventCrew_UserIsEventCrewOfAnotherEvent_ShouldReturnFalse() {
        currentUser = getLoggedInUser();

        eventCrew.setUserId(currentUser.getId());
        initEventCrewDB();

        Boolean result = managementTeamSecurityExpression.isEventCrew(OTHER_EVENT_ID);
        assertThat(result).isFalse();
    }

    private User getLoggedInUser() {
        return userService.getUserWithAuthorities()
            .orElseThrow(() -> new BadRequestException("User not login"));
    }

    private YearSession initYearSessionDB() {
        return yearSessionRepository.saveAndFlush(yearSession);
    }

    private EventCrew initEventCrewDB() {
        return eventCrewRepository.saveAndFlush(eventCrew);
    }

    private Administrator initAdministratorDB() {
        return administratorRepository.saveAndFlush(administrator);
    }
}
