package com.thirdcc.webapp.security.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private AccessTokenProvider accessTokenProvider;

    public JWTConfigurer(AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JWTFilter customFilter = new JWTFilter(accessTokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
