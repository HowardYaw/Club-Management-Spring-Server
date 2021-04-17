package com.thirdcc.webapp.annotations.authorization;

import com.thirdcc.webapp.annotations.cleanup.CleanUpNormalUser;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.security.AuthoritiesConstants;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithNormalUser.Factory.class)
@CleanUpNormalUser
public @interface WithNormalUser {

    String firstName() default "";

    String email() default "";

    String imageUrl() default "";

    String[] authorities() default {AuthoritiesConstants.USER};

    class Factory extends AbstractSecurityContextFactoryTemplate<WithNormalUser> {

        public Factory(UserRepository userRepository) {
            super(userRepository);
        }

        @Override
        public Set<String> configureAuthorityNames(WithNormalUser annotation) {
            return new HashSet<>(Arrays.asList(annotation.authorities()));
        }

        @Override
        public String createUserEmail(WithNormalUser annotation) {
            boolean hasEmail = !annotation.email().isEmpty();
            if (hasEmail) {
                return annotation.email();
            }
            return super.createUserEmail(annotation);
        }

        @Override
        public String configureFirstName(WithNormalUser annotation) {
            return annotation.firstName();
        }

        @Override
        public String configureImageUrl(WithNormalUser annotation) {
            return annotation.imageUrl();
        }

    }
}
