package com.thirdcc.webapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Clubmanagement.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private boolean shouldInitFirebase = false;

    public boolean isShouldInitFirebase() {
        return shouldInitFirebase;
    }

    public void setShouldInitFirebase(boolean shouldInitFirebase) {
        this.shouldInitFirebase = shouldInitFirebase;
    }
}
