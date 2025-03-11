package vn.edu.iuh.fit.services.interfaces;

import vn.edu.iuh.fit.models.User;

public interface IOAuthProvider {
    String verifyToken(String idToken) throws Exception;
    User getUserFromToken(String idToken) throws Exception;
}
