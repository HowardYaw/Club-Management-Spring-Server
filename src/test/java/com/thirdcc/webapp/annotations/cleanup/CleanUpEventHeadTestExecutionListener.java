package com.thirdcc.webapp.annotations.cleanup;

import com.thirdcc.webapp.repository.EventCrewRepository;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class CleanUpEventHeadTestExecutionListener implements TestExecutionListener, Ordered {

    public void afterTestMethod(TestContext testContext) throws Exception {
        ApplicationContext applicationContext = testContext.getApplicationContext();

        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
        userRepository.deleteAll();

        EventCrewRepository eventCrewRepository = applicationContext.getBean(EventCrewRepository.class);
        eventCrewRepository.deleteAll();

        EventRepository eventRepository = applicationContext.getBean(EventRepository.class);
        eventRepository.deleteAll();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
