package vn.edu.iuh.fit.services.interfaces;

public interface IOAuthVerifier {
    String verifyToken(String idToken) throws Exception;
}
