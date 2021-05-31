package com.thirdcc.webapp.config;

import com.thirdcc.webapp.service.ImageStorageService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageStorageServiceConfiguration {

    @MockBean
    ImageStorageService imageStorageService;
}
