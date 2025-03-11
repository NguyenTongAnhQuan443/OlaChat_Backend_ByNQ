package vn.edu.iuh.fit.services.Authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.enums.AuthProvider;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.services.UserService;
import vn.edu.iuh.fit.services.interfaces.IOAuthProvider;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleOAuthProvider implements IOAuthProvider {

    private final UserService userService;
    private static final Dotenv dotenv = Dotenv.load();
    private static final String CLIENT_ID = dotenv.get("GOOGLE_CLIENT_ID");

    private GoogleIdTokenVerifier getVerifier() {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    @Override
    public String verifyToken(String idToken) throws Exception {
        GoogleIdToken googleIdToken = getVerifier().verify(idToken);
        if (googleIdToken == null) {
            throw new IllegalArgumentException("Token Google không hợp lệ");
        }
        return googleIdToken.getPayload().getEmail();
    }

    @Override
    public User getUserFromToken(String idToken) throws Exception {
        GoogleIdToken googleIdToken = getVerifier().verify(idToken);
        if (googleIdToken == null) {
            throw new IllegalArgumentException("Token Google không hợp lệ");
        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();
        String displayName = (String) payload.get("name");
        String avatar = (String) payload.get("picture");

        return userService.findOrCreateUser(email, displayName, avatar, AuthProvider.GOOGLE);
    }
}
