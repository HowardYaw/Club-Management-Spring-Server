package com.thirdcc.webapp.annotations.cleanup;

import com.thirdcc.webapp.repository.*;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@TestExecutionListeners(
    value = { CleanUpCCHead.CleanUpCCHeadTestExecutionListener.class },
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public @interface CleanUpCCHead {

    class CleanUpCCHeadTestExecutionListener implements TestExecutionListener, Ordered {

        @Override
        public void afterTestMethod(TestContext testContext) throws Exception {
            ApplicationContext applicationContext = testContext.getApplicationContext();

            UserRepository userRepository = applicationContext.getBean(UserRepository.class);
            userRepository.deleteAll();

            AdministratorRepository administratorRepository = applicationContext.getBean(AdministratorRepository.class);
            administratorRepository.deleteAll();
        }

        @Override
        public int getOrder() {
            return 30000;
        }
    }
}
