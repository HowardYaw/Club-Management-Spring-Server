package com.thirdcc.webapp.authorization;

import com.thirdcc.webapp.domain.Administrator;
import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.YearSession;
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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagementTeamSecurityExpression {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final EventCrewRepository eventCrewRepository;
    private final AdministratorRepository administratorRepository;
    private final YearSessionRepository yearSessionRepository;


    public ManagementTeamSecurityExpression(
        TokenProvider tokenProvider,
        UserRepository userRepository,
        EventCrewRepository eventCrewRepository,
        AdministratorRepository administratorRepository,
        YearSessionRepository yearSessionRepository
    ) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.eventCrewRepository = eventCrewRepository;
        this.administratorRepository = administratorRepository;
        this.yearSessionRepository = yearSessionRepository;
    }

    public boolean isCurrentCCHead() {
        User currentUser = getCurrentUserWithLogin();
        YearSession currentYearSession = yearSessionRepository.findFirstByOrderByIdDesc()
            .orElseThrow(() -> new BadRequestException("Year Session not found"));
        Administrator administrator = administratorRepository
            .findByUserIdAndYearSessionAndRole(currentUser.getId(), currentYearSession, AdministratorRole.CC_HEAD)
            .orElse(null);
        return administrator != null;
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
        EventCrew eventCrew = eventCrewRepository
            .findByUserIdAndAndEventId(currentUser.getId(), eventId)
            .orElse(null);
        return eventCrew != null;
    }

    public boolean isEventHead(Long eventId) {
        User currentUser = getCurrentUserWithLogin();
        EventCrew eventCrew = eventCrewRepository
            .findByUserIdAndAndEventId(currentUser.getId(), eventId)
            .orElse(null);
        return eventCrew != null && eventCrew.getRole().equals(EventCrewRole.HEAD);
    }

    private List<String> getUserAuthRole() {
        String currentUserJWT = SecurityUtils
            .getCurrentUserJWT()
            .orElseThrow(() -> new BadRequestException("User is not login"));
        Jws<Claims> claimsJws = tokenProvider
            .getJWT()
            .parseClaimsJws(currentUserJWT);
        return Arrays
            .stream(claimsJws.getBody().get("auth").toString().split(","))
            .collect(Collectors.toList());
    }

    private User getCurrentUserWithLogin() {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));
    }
}
