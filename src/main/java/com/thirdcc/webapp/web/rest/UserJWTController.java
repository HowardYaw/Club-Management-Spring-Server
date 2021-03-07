package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.security.jwt.AccessTokenProvider;
import com.thirdcc.webapp.security.jwt.JWTFilter;
import com.thirdcc.webapp.security.jwt.RefreshTokenProvider;
import com.thirdcc.webapp.web.rest.vm.LoginVM;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final AccessTokenProvider accessTokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserJWTController(
        AccessTokenProvider accessTokenProvider,
        RefreshTokenProvider refreshTokenProvider,
        AuthenticationManagerBuilder authenticationManagerBuilder
    ) {
        this.accessTokenProvider = accessTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        String accessToken = accessTokenProvider.createToken(authentication);
        String refreshToken = refreshTokenProvider.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
        return new ResponseEntity<>(new JWTToken(accessToken, refreshToken), httpHeaders, HttpStatus.OK);
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String accessToken;
        private String refreshToken;

        public JWTToken(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
