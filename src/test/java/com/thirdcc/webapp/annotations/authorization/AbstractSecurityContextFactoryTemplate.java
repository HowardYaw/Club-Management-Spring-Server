package com.thirdcc.webapp.annotations.authorization;

import com.thirdcc.webapp.domain.Authority;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractSecurityContextFactoryTemplate<A extends Annotation> implements WithSecurityContextFactory<A> {

    private UserRepository userRepository;
    private Set<String> authorityNames;
    private String userLogin;
    private String userEmail;
    private String userPassword;
    private Set<Authority> authorities;
    private List<GrantedAuthority> grantedAuthorities;
    private User user;
    private Authentication authentication;

    public AbstractSecurityContextFactoryTemplate(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public final SecurityContext createSecurityContext(A annotation) {
        this.authorityNames = configureAuthorityNames();
        this.userLogin = createUserLogin();
        this.userEmail = createUserEmail();
        this.userPassword = createUserPassword();
        this.authorities = createAuthorities();
        this.grantedAuthorities = createGrantedAuthorities();
        this.user = createUser();
        this.authentication = createAuthentication();

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(this.authentication);

        onSecurityContextCreatedHook();
        return securityContext;
    }

    public abstract Set<String> configureAuthorityNames();

    public String createUserLogin() {
        return RandomStringUtils.randomAlphanumeric(50).toLowerCase();
    }

    public String createUserEmail() {
        return this.userLogin + "@localhost.testing";
    }

    public String createUserPassword() {
        return RandomStringUtils.random(60);
    }

    public Set<Authority> createAuthorities() {
        return this.authorityNames
            .stream()
            .map(authorityName -> {
                Authority authority = new Authority();
                authority.setName(authorityName);
                return authority;
            })
            .collect(Collectors.toSet());
    }

    public List<GrantedAuthority> createGrantedAuthorities() {
        return this.authorityNames
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    public User createUser() {
        User user = new User();
        user.setLogin(this.userLogin);
        user.setEmail(this.userEmail);
        user.setPassword(this.userPassword);
        user.setActivated(true);
        user.setAuthorities(this.authorities);
        return userRepository.saveAndFlush(user);
    }

    public Authentication createAuthentication() {
        return new UsernamePasswordAuthenticationToken(this.userLogin, this.userPassword, this.grantedAuthorities);
    }

    public void onSecurityContextCreatedHook() {};

    public User getUser() {
        return this.user;
    }
}
