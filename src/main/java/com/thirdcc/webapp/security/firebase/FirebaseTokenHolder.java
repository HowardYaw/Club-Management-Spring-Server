package com.thirdcc.webapp.security.firebase;

import com.google.api.client.util.ArrayMap;
import com.google.firebase.auth.FirebaseToken;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Objects;

public class FirebaseTokenHolder {
    private FirebaseToken token;

    public boolean isValidToken() {
        return token != null;
    }

    public FirebaseTokenHolder(FirebaseToken token) {
        this.token = token;
    }

    public String getpicture(){
        return token.getPicture();
    }

    public String getEmail() {
        return token.getEmail();
    }

    public String getIssuer() {
        return token.getIssuer();
    }

    public String getName() {
        return StringUtils.isNoneBlank(token.getName())?token.getName(): Objects.toString(token.getClaims().get("name"));
    }

    public String getUid() {
        return token.getUid();
    }

    public String getGoogleId() {
        String userId = ((ArrayList<String>) ((ArrayMap) ((ArrayMap) token.getClaims().get("firebase"))
            .get("identities")).get("google.com")).get(0);
        return userId;
    }
}
