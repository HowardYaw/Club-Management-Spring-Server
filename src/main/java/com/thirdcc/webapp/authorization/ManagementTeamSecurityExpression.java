package com.thirdcc.webapp.authorization;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.domain.enumeration.AdministratorRole;
import com.thirdcc.webapp.domain.enumeration.EventCrewRole;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.AdministratorRepository;
import com.thirdcc.webapp.repository.EventCrewRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.repository.YearSessionRepository;
import com.thirdcc.webapp.security.AuthoritiesConstants;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.security.jwt.TokenProvider;
import com.thirdcc.webapp.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ManagementTeamSecurityExpression {
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;
    private final EventCrewRepository eventCrewRepository;
    private final AdministratorRepository administratorRepository;
    private final YearSessionRepository yearSessionRepository;


    public ManagementTeamSecurityExpression(
        TokenProvider tokenProvider,
        UserService userService,
        UserRepository userRepository,
        EventCrewRepository eventCrewRepository,
        AdministratorRepository administratorRepository,
        YearSessionRepository yearSessionRepository
    ) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.userRepository = userRepository;
        this.eventCrewRepository = eventCrewRepository;
        this.administratorRepository = administratorRepository;
        this.yearSessionRepository = yearSessionRepository;
    }

    public boolean isCurrentCCHead() {
        User currentUser = getCurrentUserWithLogin();
        YearSession currentYearSession = yearSessionRepository.findFirstByOrderByIdDesc()
            .orElseThrow(() -> new BadRequestException("Year Session not found"));
        return administratorRepository
            .findByUserIdAndYearSessionAndRole(currentUser.getId(), currentYearSession, AdministratorRole.CC_HEAD)
            .isPresent();
    }

    public boolean hasRoleAdminOrIsEventCrew(Long eventId) {
        if (getUserAuthRole().contains(AuthoritiesConstants.ADMIN)) {
            return true;
        } else {
            return isEventCrew(eventId);
        }
    }

    public boolean isEventCrew(Long eventId) {
        User currentUser = getCurrentUserWithLogin();
        return eventCrewRepository
            .findByUserIdAndAndEventId(currentUser.getId(), eventId)
            .isPresent();
    }

    public boolean isEventHead(Long eventId) {
        User currentUser = getCurrentUserWithLogin();
        EventCrew eventCrew = eventCrewRepository
            .findByUserIdAndAndEventId(currentUser.getId(), eventId)
            .orElse(null);
        return eventCrew != null && eventCrew.getRole().equals(EventCrewRole.HEAD);
    }

    public boolean hasRoleAdminOrIsEventHead(Long eventId) {
        if (getUserAuthRole().contains(AuthoritiesConstants.ADMIN)) {
            return true;
        } else {
            return isEventHead(eventId);
        }
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
