package vn.edu.iuh.fit.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.fit.enums.AuthProvider;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;
import vn.edu.iuh.fit.utils.JwtUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacebookAuthService {

    private UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public String verifyFacebookToken(String accessToken) throws Exception {
        String url = "https://graph.facebook.com/me?fields=id,name,email,picture&access_token=" + accessToken;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException("Token Facebook không hợp lệ");
        }

        JsonNode userNode = new ObjectMapper().readTree(response.getBody());
        String facebookId = userNode.get("id").asText();
        String email = userNode.has("email") ? userNode.get("email").asText() : null;

        Optional<User> userOpt = userRepository.findUserByEmail(email);
        User user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            user = User.builder()
                    .email(email)
                    .displayName(userNode.get("name").asText())
                    .avatar(userNode.get("picture").get("data").get("url").asText())
                    .authProvider(AuthProvider.FACEBOOK)
                    .externalId(facebookId)
                    .build();
            userRepository.save(user);
        }

        return jwtUtil.generateToken(user.getId());
    }

}
