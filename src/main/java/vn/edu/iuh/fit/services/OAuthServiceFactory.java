package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.services.interfaces.IOAuthVerifier;
import vn.edu.iuh.fit.services.interfaces.IUserProvider;

import java.util.Map;

@Component
public class OAuthServiceFactory {

    private final GoogleOAuthService googleOAuthService;
    private final Map<String, IOAuthVerifier> verifierMap;
    private final Map<String, IUserProvider> userProviderMap;

    public OAuthServiceFactory(GoogleOAuthService googleOAuthService, Map<String, IOAuthVerifier> verifierMap, Map<String, IUserProvider> userProviderMap) {
        this.googleOAuthService = googleOAuthService;
        this.verifierMap = Map.of(
                "GOOGLE", googleOAuthService
        );

        this.userProviderMap = Map.of(
                "GOOGLE", googleOAuthService
        );
    }

    public IOAuthVerifier getOAuthVerifier(String provider) {
        return verifierMap.get(provider.toUpperCase());
    }

    public IUserProvider getUserProvider(String provider) {
        return userProviderMap.get(provider.toUpperCase());
    }
}
