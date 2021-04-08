package com.thirdcc.webapp.annotations.authorization;

import com.thirdcc.webapp.security.AuthoritiesConstants;

public interface WithCustomUser {

    String firstName();

    String email();

    String imageUrl();

    String[] authorities();
}
