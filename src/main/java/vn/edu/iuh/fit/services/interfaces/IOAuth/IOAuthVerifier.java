package vn.edu.iuh.fit.services.interfaces.IOAuth;

public interface IOAuthVerifier {
    String verifyToken(String idToken) throws Exception;
}
