package com.thirdcc.webapp.annotations.init;

import com.thirdcc.webapp.domain.YearSession;
import com.thirdcc.webapp.repository.YearSessionRepository;
import com.thirdcc.webapp.utils.YearSessionUtils;
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
    value = { InitYearSession.InitYearSessionTestExecutionListener.class },
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public @interface InitYearSession {

    class InitYearSessionTestExecutionListener implements TestExecutionListener, Ordered {

        @Override
        public void beforeTestClass(TestContext testContext) throws Exception {
            ApplicationContext applicationContext = testContext.getApplicationContext();

            YearSessionRepository yearSessionRepository = applicationContext.getBean(YearSessionRepository.class);
            YearSession yearSession = new YearSession();
            yearSession.setValue(YearSessionUtils.getCurrentYearSession());
            yearSessionRepository.saveAndFlush(yearSession);
        }

        @Override
        public void afterTestClass(TestContext testContext) throws Exception {
            ApplicationContext applicationContext = testContext.getApplicationContext();

            YearSessionRepository yearSessionRepository = applicationContext.getBean(YearSessionRepository.class);
            yearSessionRepository.deleteAll();
        }

        @Override
        public int getOrder() {
            return -30000;
        }
    }
}
