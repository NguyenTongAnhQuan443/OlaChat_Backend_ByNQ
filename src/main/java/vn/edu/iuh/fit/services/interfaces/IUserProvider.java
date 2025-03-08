package vn.edu.iuh.fit.services.interfaces;

import vn.edu.iuh.fit.models.User;

public interface IUserProvider {
    User getUserFromToken(String idToken) throws Exception;
}
