package com.thirdcc.webapp.security.firebase;

import com.thirdcc.webapp.security.firebase.exception.FirebaseTokenException;
import com.thirdcc.webapp.security.firebase.exception.FirebaseTokenExceptionMessages;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService{
    public FirebaseTokenHolder parseToken(String firebaseToken) throws InterruptedException, ExecutionException, FirebaseException {
        if (StringUtils.isBlank(firebaseToken)) {
            throw new FirebaseTokenException(FirebaseTokenExceptionMessages.TOKEN_HEADER_NOT_FOUND);
        }

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(firebaseToken).get();
        return new FirebaseTokenHolder(decodedToken);
    }
}
