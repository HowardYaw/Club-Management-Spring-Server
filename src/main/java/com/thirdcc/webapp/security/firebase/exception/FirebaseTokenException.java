package com.thirdcc.webapp.security.firebase.exception;

public class FirebaseTokenException extends RuntimeException {

    String message;

    public FirebaseTokenException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

