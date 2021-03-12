package com.thirdcc.webapp.utils;

import com.google.firebase.FirebaseException;
import com.google.gson.Gson;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.security.firebase.FirebaseAuthenticationToken;
import com.thirdcc.webapp.security.firebase.FirebaseService;
import com.thirdcc.webapp.security.firebase.FirebaseTokenHolder;
import com.thirdcc.webapp.security.firebase.exception.FirebaseTokenExceptionMessages;
import com.thirdcc.webapp.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.thirdcc.webapp.security.firebase.FirebaseConstants.AUTHORIZATION_HEADER_NAME;

public class FirebaseUtils {

    private static Logger logger = LoggerFactory.getLogger(FirebaseUtils.class);

    public static Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response, FirebaseService firebaseService) throws IOException {
        String xAuth = request.getHeader(AUTHORIZATION_HEADER_NAME);
        if (StringUtils.isBlank(xAuth)) {
            ProblemBuilder builder = Problem.builder()
                .withTitle("Token header not found")
                .withDetail(FirebaseTokenExceptionMessages.TOKEN_HEADER_NOT_FOUND);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(new Gson().toJson(builder.build()));
            return null;
        }

        FirebaseTokenHolder holder = null;
        try {
            holder = firebaseService.parseToken(xAuth);
        } catch (InterruptedException | ExecutionException | FirebaseException e) {
            ProblemBuilder builder = Problem.builder()
                .withTitle("Firebase Auth Error")
                .withDetail(e.getLocalizedMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(new Gson().toJson(builder.build()));
            logger.error("error", e);
            return null;
        }

        //Firebase ID token has expired or is not yet valid
        if (!holder.isValidToken()) {
            ProblemBuilder builder = Problem.builder()
                .withTitle("Token has expired")
                .withDetail(FirebaseTokenExceptionMessages.TOKEN_EXPIRED);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(new Gson().toJson(builder.build()));
            return null;
        }

        return new FirebaseAuthenticationToken(holder.getUid(), holder);
    }

    public static Authentication authenticateOrRegisterFirebaseUser(Authentication authentication, UserService userService) throws AuthenticationException {
        FirebaseAuthenticationToken authenticationToken = (FirebaseAuthenticationToken) authentication;
        User details = userService.getUserWithAuthoritiesByLogin(((FirebaseTokenHolder) authenticationToken.getCredentials()).getUid()).orElse(null);
        if (details == null) {
            FirebaseTokenHolder firebaseTokenHolder = (FirebaseTokenHolder) authenticationToken.getCredentials();
            details = userService.registerFirebaseUser(
                firebaseTokenHolder.getName(),
                firebaseTokenHolder.getUid(),
                firebaseTokenHolder.getEmail(),
                firebaseTokenHolder.getpicture());
        }

        authenticationToken = new FirebaseAuthenticationToken(details.getLogin(), authentication.getCredentials(),
            details.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList()));

        return authenticationToken;
    }
}
