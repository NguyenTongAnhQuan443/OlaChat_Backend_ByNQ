////package vn.edu.iuh.fit.services;
////
////import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
////import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
////import com.google.api.client.http.javanet.NetHttpTransport;
////import com.google.api.client.json.jackson2.JacksonFactory;
////import io.github.cdimascio.dotenv.Dotenv;
////import lombok.RequiredArgsConstructor;
////import org.springframework.stereotype.Service;
////import vn.edu.iuh.fit.enums.Role;
////import vn.edu.iuh.fit.enums.UserStatus;
////import vn.edu.iuh.fit.models.User;
////import vn.edu.iuh.fit.repositories.UserRepository;
////import vn.edu.iuh.fit.enums.AuthProvider;
////import vn.edu.iuh.fit.utils.JwtUtil;
////
////import java.util.Collections;
////import java.util.Optional;
////
////@Service
////@RequiredArgsConstructor
////public class GoogleAuthService {
////
////    private final UserRepository userRepository;
////    private final JwtUtil jwtUtil;
////
////    private static final Dotenv dotenv = Dotenv.load();
////    private static final String CLIENT_ID = dotenv.get("GOOGLE_CLIENT_ID");
////
////    public String verifyGoogleToken(String idToken) throws Exception {
////        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
////                new NetHttpTransport(), JacksonFactory.getDefaultInstance())
////                .setAudience(Collections.singletonList(CLIENT_ID))
////                .build();
////
////        GoogleIdToken googleIdToken = verifier.verify(idToken);
////        if (googleIdToken == null) {
////            throw new IllegalArgumentException("Token Google không hợp lệ");
////        }
////
////        GoogleIdToken.Payload payload = googleIdToken.getPayload();
////        String email = payload.getEmail();
////        String googleId = payload.getSubject();
////
////        Optional<User> userOpt = userRepository.findUserByEmail(email);
////        User user;
////        if (userOpt.isPresent()) {
////            user = userOpt.get();
////        } else {
////            user = User.builder()
////                    .email(email)
////                    .displayName((String) payload.get("name"))
////                    .username((String) payload.get("name"))
////                    .avatar((String) payload.get("picture"))
////                    .authProvider(AuthProvider.GOOGLE)
////                    .role(Role.USER)
////                    .status(UserStatus.ACTIVE)
////                    .build();
////            userRepository.save(user);
////        }
////
////        return jwtUtil.generateToken(user.getId());
////    }
////
////    public User getUserFromToken(String idToken) throws Exception {
////        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
////                new NetHttpTransport(), JacksonFactory.getDefaultInstance())
////                .setAudience(Collections.singletonList(CLIENT_ID))
////                .build();
////
////        GoogleIdToken googleIdToken = verifier.verify(idToken);
////        if (googleIdToken == null) {
////            throw new IllegalArgumentException("Token Google không hợp lệ");
////        }
////
////        GoogleIdToken.Payload payload = googleIdToken.getPayload();
////        String email = payload.getEmail();
////        String googleId = payload.getSubject();
////
////        Optional<User> userOpt = userRepository.findUserByEmail(email);
////        User user;
////        if (userOpt.isPresent()) {
////            user = userOpt.get();
////        } else {
////            user = User.builder()
////                    .email(email)
////                    .displayName((String) payload.get("name"))
////                    .username((String) payload.get("name"))
////                    .avatar((String) payload.get("picture"))
////                    .authProvider(AuthProvider.GOOGLE)
////                    .role(Role.USER)
////                    .status(UserStatus.ACTIVE)
////                    .build();
////            userRepository.save(user);
////        }
////
////        return user;
////    }
////
////}
////
//
//package vn.edu.iuh.fit.services;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import io.github.cdimascio.dotenv.Dotenv;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import vn.edu.iuh.fit.enums.AuthProvider;
//import vn.edu.iuh.fit.enums.Role;
//import vn.edu.iuh.fit.enums.UserStatus;
//import vn.edu.iuh.fit.models.User;
//import vn.edu.iuh.fit.repositories.UserRepository;
//import vn.edu.iuh.fit.services.interfaces.OAuthService;
//import vn.edu.iuh.fit.utils.JwtUtil;
//
//import java.util.Collections;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class GoogleOAuthService implements OAuthService {
//
//    private final UserRepository userRepository;
//    private final JwtUtil jwtUtil;
//
//    private static final Dotenv dotenv = Dotenv.load();
//    private static final String CLIENT_ID = dotenv.get("GOOGLE_CLIENT_ID");
//
//    private GoogleIdTokenVerifier getVerifier() {
//        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
//                .setAudience(Collections.singletonList(CLIENT_ID))
//                .build();
//    }
//
//    @Override
//    public String verifyToken(String idToken) throws Exception {
//        GoogleIdToken googleIdToken = getVerifier().verify(idToken);
//        if (googleIdToken == null) {
//            throw new IllegalArgumentException("Token Google không hợp lệ");
//        }
//        return googleIdToken.getPayload().getEmail();
//    }
//
//    @Override
//    public User getUserFromToken(String idToken) throws Exception {
//        GoogleIdToken googleIdToken = getVerifier().verify(idToken);
//        if (googleIdToken == null) {
//            throw new IllegalArgumentException("Token Google không hợp lệ");
//        }
//
//        GoogleIdToken.Payload payload = googleIdToken.getPayload();
//        String email = payload.getEmail();
//
//        return userRepository.findUserByEmail(email).orElseGet(() -> {
//            User newUser = User.builder()
//                    .email(email)
//                    .displayName((String) payload.get("name"))
//                    .avatar((String) payload.get("picture"))
//                    .authProvider(AuthProvider.GOOGLE)
//                    .role(Role.USER)
//                    .status(UserStatus.ACTIVE)
//                    .build();
//            return userRepository.save(newUser);
//        });
//    }
//}

//package vn.edu.iuh.fit.services;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import io.github.cdimascio.dotenv.Dotenv;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import vn.edu.iuh.fit.enums.Role;
//import vn.edu.iuh.fit.enums.UserStatus;
//import vn.edu.iuh.fit.models.User;
//import vn.edu.iuh.fit.repositories.UserRepository;
//import vn.edu.iuh.fit.enums.AuthProvider;
//import vn.edu.iuh.fit.utils.JwtUtil;
//
//import java.util.Collections;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class GoogleAuthService {
//
//    private final UserRepository userRepository;
//    private final JwtUtil jwtUtil;
//
//    private static final Dotenv dotenv = Dotenv.load();
//    private static final String CLIENT_ID = dotenv.get("GOOGLE_CLIENT_ID");
//
//    public String verifyGoogleToken(String idToken) throws Exception {
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
//                new NetHttpTransport(), JacksonFactory.getDefaultInstance())
//                .setAudience(Collections.singletonList(CLIENT_ID))
//                .build();
//
//        GoogleIdToken googleIdToken = verifier.verify(idToken);
//        if (googleIdToken == null) {
//            throw new IllegalArgumentException("Token Google không hợp lệ");
//        }
//
//        GoogleIdToken.Payload payload = googleIdToken.getPayload();
//        String email = payload.getEmail();
//        String googleId = payload.getSubject();
//
//        Optional<User> userOpt = userRepository.findUserByEmail(email);
//        User user;
//        if (userOpt.isPresent()) {
//            user = userOpt.get();
//        } else {
//            user = User.builder()
//                    .email(email)
//                    .displayName((String) payload.get("name"))
//                    .username((String) payload.get("name"))
//                    .avatar((String) payload.get("picture"))
//                    .authProvider(AuthProvider.GOOGLE)
//                    .role(Role.USER)
//                    .status(UserStatus.ACTIVE)
//                    .build();
//            userRepository.save(user);
//        }
//
//        return jwtUtil.generateToken(user.getId());
//    }
//
//    public User getUserFromToken(String idToken) throws Exception {
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
//                new NetHttpTransport(), JacksonFactory.getDefaultInstance())
//                .setAudience(Collections.singletonList(CLIENT_ID))
//                .build();
//
//        GoogleIdToken googleIdToken = verifier.verify(idToken);
//        if (googleIdToken == null) {
//            throw new IllegalArgumentException("Token Google không hợp lệ");
//        }
//
//        GoogleIdToken.Payload payload = googleIdToken.getPayload();
//        String email = payload.getEmail();
//        String googleId = payload.getSubject();
//
//        Optional<User> userOpt = userRepository.findUserByEmail(email);
//        User user;
//        if (userOpt.isPresent()) {
//            user = userOpt.get();
//        } else {
//            user = User.builder()
//                    .email(email)
//                    .displayName((String) payload.get("name"))
//                    .username((String) payload.get("name"))
//                    .avatar((String) payload.get("picture"))
//                    .authProvider(AuthProvider.GOOGLE)
//                    .role(Role.USER)
//                    .status(UserStatus.ACTIVE)
//                    .build();
//            userRepository.save(user);
//        }
//
//        return user;
//    }
//
//}
//

package vn.edu.iuh.fit.services.OAuthServices;

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
import vn.edu.iuh.fit.services.interfaces.IOAuth.IOAuthVerifier;
import vn.edu.iuh.fit.services.interfaces.IOAuth.IUserProvider;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService implements IOAuthVerifier, IUserProvider {

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
