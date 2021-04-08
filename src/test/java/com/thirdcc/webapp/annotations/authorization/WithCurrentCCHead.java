package com.thirdcc.webapp.annotations.authorization;

import com.thirdcc.webapp.annotations.cleanup.CleanUpCCHead;
import com.thirdcc.webapp.domain.Administrator;
import com.thirdcc.webapp.domain.YearSession;
import com.thirdcc.webapp.domain.enumeration.AdministratorRole;
import com.thirdcc.webapp.domain.enumeration.AdministratorStatus;
import com.thirdcc.webapp.repository.AdministratorRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.security.AuthoritiesConstants;
import com.thirdcc.webapp.service.YearSessionService;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;
import java.util.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCurrentCCHead.Factory.class)
@CleanUpCCHead
public @interface WithCurrentCCHead {

    String firstName() default "";

    String email() default "";

    String imageUrl() default "";

    String[] authorities() default {AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER};

    class Factory extends AbstractSecurityContextFactoryTemplate<WithCurrentCCHead> {

        private final AdministratorRepository administratorRepository;
        private final YearSessionService yearSessionService;

        public Factory(
            UserRepository userRepository,
            AdministratorRepository administratorRepository,
            YearSessionService yearSessionService
        ) {
            super(userRepository);
            this.administratorRepository = administratorRepository;
            this.yearSessionService = yearSessionService;
        }

        @Override
        public Set<String> configureAuthorityNames(WithCurrentCCHead annotation) {
            return new HashSet<>(Arrays.asList(annotation.authorities()));
        }

        @Override
        public String createUserEmail(WithCurrentCCHead annotation) {
            boolean hasEmail = !annotation.email().isEmpty();
            if (hasEmail) {
                return annotation.email();
            }
            return super.createUserEmail(annotation);
        }

        @Override
        public String configureFirstName(WithCurrentCCHead annotation) {
            return annotation.firstName();
        }

        @Override
        public String configureImageUrl(WithCurrentCCHead annotation) {
            return annotation.imageUrl();
        }

        @Override
        public void onSecurityContextCreatedHook() {
            YearSession currentYearSession = yearSessionService.getCurrentYearSession();
            Administrator savedAdministrator = initAdministratorDB(currentYearSession);
        }

        private Administrator initAdministratorDB(YearSession yearSession) {
            Administrator administrator = new Administrator();
            administrator.setUserId(getUser().getId());
            administrator.setRole(AdministratorRole.CC_HEAD);
            administrator.setStatus(AdministratorStatus.ACTIVE);
            administrator.setYearSession(yearSession.getValue());
            return administratorRepository.saveAndFlush(administrator);
        }
    }
}
