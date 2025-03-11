package vn.edu.iuh.fit.services.Authentication;

import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.services.interfaces.IOAuthService;

import java.util.Map;

@Component
public class OAuthProviderFactory {
    private final Map<String, IOAuthService> oauthServices;

    public OAuthProviderFactory(Map<String, IOAuthService> oauthServices) {
        this.oauthServices = oauthServices;
    }

    public IOAuthService getOAuthService(String provider) {
        return oauthServices.get(provider.toUpperCase());
    }
}
