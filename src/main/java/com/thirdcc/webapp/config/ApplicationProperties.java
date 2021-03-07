package com.thirdcc.webapp.config;

import io.github.jhipster.config.JHipsterProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Clubmanagement.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final ApplicationProperties.AccessToken accessToken = new ApplicationProperties.AccessToken();
    private final ApplicationProperties.RefreshToken refreshToken = new ApplicationProperties.RefreshToken();

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public static class AccessToken {
        private String base64Secret = "";
        private Long tokenValidityInSeconds = 0L;

        public AccessToken() {
        }

        public String getBase64Secret() {
            return base64Secret;
        }

        public void setBase64Secret(String base64Secret) {
            this.base64Secret = base64Secret;
        }

        public Long getTokenValidityInSeconds() {
            return tokenValidityInSeconds;
        }

        public void setTokenValidityInSeconds(Long tokenValidityInSeconds) {
            this.tokenValidityInSeconds = tokenValidityInSeconds;
        }
    }

    public static class RefreshToken {
        private String base64Secret = "";
        private Long tokenValidityInSeconds = 0L;

        public RefreshToken() {
        }

        public String getBase64Secret() {
            return base64Secret;
        }

        public void setBase64Secret(String base64Secret) {
            this.base64Secret = base64Secret;
        }

        public Long getTokenValidityInSeconds() {
            return tokenValidityInSeconds;
        }

        public void setTokenValidityInSeconds(Long tokenValidityInSeconds) {
            this.tokenValidityInSeconds = tokenValidityInSeconds;
        }
    }

}
