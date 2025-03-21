package vn.edu.iuh.fit.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.enums.AuthProvider;
import vn.edu.iuh.fit.models.User;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final String googleClientId = System.getenv("GOOGLE_CLIENT_ID");
    private final UserService userService;

    public User authenticateWithGoogle(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) return null;

            GoogleIdToken.Payload payload = token.getPayload();
            return userService.findOrCreateUser(
                    payload.getEmail(),
                    (String) payload.get("name"),
                    (String) payload.get("picture"),
                    AuthProvider.GOOGLE);
        } catch (Exception e) {
            return null;
        }
    }
}