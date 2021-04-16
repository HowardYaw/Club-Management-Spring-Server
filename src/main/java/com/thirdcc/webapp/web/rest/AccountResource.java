package com.thirdcc.webapp.web.rest;


import com.thirdcc.webapp.authorization.ManagementTeamSecurityExpression;
import com.thirdcc.webapp.domain.Authority;
import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.enumeration.EventCrewRole;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.EventCrewRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.MailService;
import com.thirdcc.webapp.service.UserService;
import com.thirdcc.webapp.service.UserUniInfoService;
import com.thirdcc.webapp.service.dto.AccountDetailsDTO;
import com.thirdcc.webapp.service.dto.PasswordChangeDTO;
import com.thirdcc.webapp.service.dto.UserDTO;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;
import com.thirdcc.webapp.web.rest.errors.*;
import com.thirdcc.webapp.web.rest.vm.KeyAndPasswordVM;
import com.thirdcc.webapp.web.rest.vm.ManagedUserVM;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final UserUniInfoService userUniInfoService;

    private final MailService mailService;

    private final ManagementTeamSecurityExpression managementTeamSecurityExpression;

    private final EventCrewRepository eventCrewRepository;

    public AccountResource(
        UserRepository userRepository,
        UserService userService,
        UserUniInfoService userUniInfoService, MailService mailService,
        ManagementTeamSecurityExpression managementTeamSecurityExpression,
        EventCrewRepository eventCrewRepository
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userUniInfoService = userUniInfoService;
        this.mailService = mailService;
        this.managementTeamSecurityExpression = managementTeamSecurityExpression;
        this.eventCrewRepository = eventCrewRepository;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException  {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        mailService.sendActivationEmail(user);
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    @GetMapping("/account")
    public AccountDetailsDTO getAccountDetails() {
        User user = userService
            .getUserWithAuthorities()
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
        Set<String> authorityNames = user
            .getAuthorities()
            .stream()
            .map(Authority::getName)
            .collect(Collectors.toSet());
        boolean isCurrentCCHead = managementTeamSecurityExpression.isCurrentCCHead();
        boolean isCurrentAdministrator = managementTeamSecurityExpression.isCurrentAdministrator();
        Set<Long> eventHeadEventIds = eventCrewRepository
            .findAllByUserIdAndRole(user.getId(), EventCrewRole.HEAD)
            .stream()
            .map(EventCrew::getEventId)
            .collect(Collectors.toSet());
        Set<Long> eventCrewEventIds = eventCrewRepository
            .findAllByUserId(user.getId())
            .stream()
            .map(EventCrew::getEventId)
            .collect(Collectors.toSet());

        AccountDetailsDTO dto = new AccountDetailsDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setEmail(user.getEmail());
        dto.setImageUrl(user.getImageUrl());
        dto.setAuthorities(authorityNames);
        dto.setCurrentCCHead(isCurrentCCHead);
        dto.setCurrentAdministrator(isCurrentAdministrator);
        dto.setEventHeadEventIds(eventHeadEventIds);
        dto.setEventCrewEventIds(eventCrewEventIds);
        return dto;
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException          {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }
        userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
            userDTO.getLangKey(), userDTO.getImageUrl());
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     * @throws EmailNotFoundException {@code 400 (Bad Request)} if the email address is not registered.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        mailService.sendPasswordResetMail(
            userService.requestPasswordReset(mail)
                .orElseThrow(EmailNotFoundException::new)
        );
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException         {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user =
            userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    @GetMapping("/account/is-profile-completed")
    public ResponseEntity<Map<String, Boolean>> isProfileCompleted() {
        User currentUser = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new BadRequestException("Cannot find user"));
        boolean isBasicProfileCompleted = userService.isBasicProfileCompleted(currentUser.getId());
        boolean isUserUniInfoCompleted = userUniInfoService.isUserUniInfoCompleted(currentUser.getId());
        boolean isProfileCompleted = isBasicProfileCompleted && isUserUniInfoCompleted;
        Map<String, Boolean> result = new HashMap<>();
        result.put("isProfileCompleted", isProfileCompleted);
        return ResponseEntity.ok().headers(null).body(result);
    }

    @PostMapping("/account/profile")
    public ResponseEntity<UserUniInfoDTO> completeProfile(@RequestBody UserUniInfoDTO userUniInfoDTO) {
        userService.updateUser(userUniInfoDTO);
        UserUniInfoDTO result = userUniInfoService.save(userUniInfoDTO);
        return ResponseEntity.ok().headers(null).body(result);
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
