package com.thirdcc.webapp.authorization;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.domain.enumeration.AdministratorRole;
import com.thirdcc.webapp.domain.enumeration.AdministratorStatus;
import com.thirdcc.webapp.domain.enumeration.EventCrewRole;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.AdministratorRepository;
import com.thirdcc.webapp.repository.EventCrewRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.repository.YearSessionRepository;
import com.thirdcc.webapp.security.AuthoritiesConstants;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagementTeamSecurityExpression {
    private final UserService userService;
    private final UserRepository userRepository;
    private final EventCrewRepository eventCrewRepository;
    private final AdministratorRepository administratorRepository;
    private final YearSessionRepository yearSessionRepository;


    public ManagementTeamSecurityExpression(
        UserService userService,
        UserRepository userRepository,
        EventCrewRepository eventCrewRepository,
        AdministratorRepository administratorRepository,
        YearSessionRepository yearSessionRepository
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.eventCrewRepository = eventCrewRepository;
        this.administratorRepository = administratorRepository;
        this.yearSessionRepository = yearSessionRepository;
    }

    /**
     * Check if User is CC Head only
     */
    public boolean isCurrentCCHead() {
        User currentUser = getCurrentUserWithLogin();
        YearSession currentYearSession = yearSessionRepository.findFirstByOrderByIdDesc()
            .orElseThrow(() -> new BadRequestException("Year Session not found"));
        return administratorRepository
            .findByUserIdAndYearSessionAndRoleAndStatus(currentUser.getId(), currentYearSession.getValue(), AdministratorRole.CC_HEAD, AdministratorStatus.ACTIVE)
            .isPresent();
    }

    /**
     * Check if User is Administrator
     */
    public boolean isCurrentAdministrator() {
        User currentUser = getCurrentUserWithLogin();
        YearSession currentYearSession = yearSessionRepository.findFirstByOrderByIdDesc()
            .orElseThrow(() -> new BadRequestException("Year Session not found"));
        return administratorRepository
            .findByUserIdAndYearSessionAndStatus(currentUser.getId(), currentYearSession.getValue(), AdministratorStatus.ACTIVE)
            .isPresent();
    }

    public boolean hasRoleAdminOrIsEventCrew(Long eventId) {
        if (getUserAuthRole().contains(AuthoritiesConstants.ADMIN)) {
            return true;
        } else {
            return isEventCrew(eventId);
        }
    }

    /**
     * Check if User is Event Crew
     * @param eventId
     */
    public boolean isEventCrew(Long eventId) {
        User currentUser = getCurrentUserWithLogin();
        return eventCrewRepository
            .findByUserIdAndAndEventId(currentUser.getId(), eventId)
            .isPresent();
    }

    /**
     * Check if User is Event Head only
     * @param eventId
     */
    public boolean isEventHead(Long eventId) {
        User currentUser = getCurrentUserWithLogin();
        return eventCrewRepository
            .findByUserIdAndAndEventIdAndRole(currentUser.getId(), eventId, EventCrewRole.HEAD)
            .isPresent();
    }

    public boolean hasRoleAdminOrIsEventHead(Long eventId) {
        if (getUserAuthRole().contains(AuthoritiesConstants.ADMIN)) {
            return true;
        } else {
            return isEventHead(eventId);
        }
    }

    /**
     * Check if User is Administrator role or Event Head
     * @param eventId
     */
    public boolean isEventHeadOrAdministrator(Long eventId) {
        return isCurrentAdministrator() || isEventHead(eventId);
    }

    /**
     * Check if User is Administrator role or Event Crew
     * @param eventId
     */
    public boolean isEventCrewOrAdministrator(Long eventId) {
        return isCurrentAdministrator() || isEventCrew(eventId);
    }

    private List<String> getUserAuthRole() {
        User currentUser = userService.getUserWithAuthorities()
            .orElseThrow(() -> new BadRequestException("User is not login"));
        return currentUser.getAuthorities()
            .stream()
            .map(Authority::getName)
            .collect(Collectors.toList());
    }

    private User getCurrentUserWithLogin() {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));
    }
}
