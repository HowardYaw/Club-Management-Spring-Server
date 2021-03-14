package com.thirdcc.webapp.annotations.authorization;

import com.thirdcc.webapp.domain.Administrator;
import com.thirdcc.webapp.domain.YearSession;
import com.thirdcc.webapp.domain.enumeration.AdministratorRole;
import com.thirdcc.webapp.domain.enumeration.AdministratorStatus;
import com.thirdcc.webapp.repository.AdministratorRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.repository.YearSessionRepository;
import com.thirdcc.webapp.security.AuthoritiesConstants;
import com.thirdcc.webapp.utils.YearSessionUtils;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;
import java.util.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCurrentCCHeadMockUser.Factory.class)
public @interface WithCurrentCCHeadMockUser {
//
//    String CC_ADMINISTRATOR_LOGIN = "cc_administrator";
//    String CC_ADMINISTRATOR_EMAIL = "cc_administrator@testing.com";
//    String CC_ADMINISTRATOR_PASS = RandomStringUtils.random(60);

    class Factory extends AbstractSecurityContextFactoryTemplate<WithCurrentCCHeadMockUser> {

        private final AdministratorRepository administratorRepository;
        private final YearSessionRepository yearSessionRepository;

        public Factory(UserRepository userRepository, AdministratorRepository administratorRepository, YearSessionRepository yearSessionRepository) {
            super(userRepository);
            this.administratorRepository = administratorRepository;
            this.yearSessionRepository = yearSessionRepository;
        }

        @Override
        public Set<String> configureAuthorityNames() {
            return new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER));
        }

//        @Override
//        public String configureUserLogin() {
//            return CC_ADMINISTRATOR_LOGIN;
//        }
//
//        @Override
//        public String configureUserEmail() {
//            return CC_ADMINISTRATOR_EMAIL;
//        }
//
//        @Override
//        public String configureUserPassword() {
//            return CC_ADMINISTRATOR_PASS;
//        }

        @Override
        public void onSecurityContextCreatedHook() {
            YearSession savedYearSession = initYearSessionDB();
            Administrator savedAdministrator = initAdministratorDB(savedYearSession);
        }

        private YearSession initYearSessionDB() {
            YearSession yearSession = new YearSession();
            yearSession.setValue(YearSessionUtils.getCurrentYearSession());
            return yearSessionRepository.saveAndFlush(yearSession);
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
