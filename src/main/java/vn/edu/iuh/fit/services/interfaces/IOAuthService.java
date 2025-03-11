package vn.edu.iuh.fit.services.interfaces;

import vn.edu.iuh.fit.models.User;

public interface IOAuthService {
    String verifyToken(String idToken) throws Exception;
    User getUserFromToken(String idToken) throws Exception;
}
