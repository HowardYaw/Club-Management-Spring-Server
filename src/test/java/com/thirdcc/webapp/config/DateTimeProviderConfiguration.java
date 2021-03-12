package com.thirdcc.webapp.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;

@Configuration
public class DateTimeProviderConfiguration {

    @MockBean
    public DateTimeProvider dateTimeProvider;
}
