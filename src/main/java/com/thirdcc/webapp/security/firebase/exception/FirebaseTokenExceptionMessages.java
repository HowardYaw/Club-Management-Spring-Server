package com.thirdcc.webapp.security.firebase.exception;

public class FirebaseTokenExceptionMessages {

    public static final String TOKEN_HEADER_NOT_FOUND = "You must send X-Authorization-Firebase field in header to authorize.";
    public static final String TOKEN_EXPIRED = "Firebase ID token is not yet valid Or something goes wrong at backend";

}
