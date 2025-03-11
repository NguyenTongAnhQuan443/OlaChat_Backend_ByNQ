package vn.edu.iuh.fit.services.Authentication;

import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.services.interfaces.IOAuthProvider;

import java.util.Map;

@Component
public class OAuthProviderFactory {
    private final Map<String, IOAuthProvider> oauthServices;

    public OAuthProviderFactory(Map<String, IOAuthProvider> oauthServices) {
        this.oauthServices = oauthServices;
    }

    public IOAuthProvider getOAuthService(String provider) {
        return oauthServices.get(provider.toUpperCase());
    }
}
