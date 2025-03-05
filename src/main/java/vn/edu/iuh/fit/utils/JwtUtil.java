package vn.edu.iuh.fit.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final PrivateKey PRIVATE_KEY;
    private static final PublicKey PUBLIC_KEY;

    static {
        try {
            PRIVATE_KEY = loadPrivateKey("private.pem");
            PUBLIC_KEY = loadPublicKey("public.pem");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tải khóa RSA", e);
        }
    }

    private static PrivateKey loadPrivateKey(String filename) throws Exception {
        InputStream inputStream = JwtUtil.class.getClassLoader().getResourceAsStream("certs/" + filename);
        if (inputStream == null) {
            throw new FileNotFoundException("Không tìm thấy file: certs/" + filename);
        }

        // Đọc file thành chuỗi
        String privateKeyPEM = new String(inputStream.readAllBytes());

        // Xóa bỏ phần header/footer và các ký tự xuống dòng
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");  // Xóa tất cả khoảng trắng và dòng trống

        // Giải mã Base64
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);

        // Tạo key spec và load private key
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private static PublicKey loadPublicKey(String filename) throws Exception {
        InputStream inputStream = JwtUtil.class.getClassLoader().getResourceAsStream("certs/" + filename);
        if (inputStream == null) {
            throw new FileNotFoundException("Không tìm thấy file: certs/" + filename);
        }

        // Đọc file thành chuỗi
        String publicKeyPEM = new String(inputStream.readAllBytes());

        // Xóa bỏ phần header/footer và các ký tự xuống dòng
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");  // Xóa khoảng trắng và dòng trống

        // Giải mã Base64
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);

        // Tạo key spec và load public key
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(PUBLIC_KEY)  // Dùng public key để xác minh
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(String phoneNumber) {
        return Jwts.builder()
                .setSubject(phoneNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ
                .signWith(PRIVATE_KEY, SignatureAlgorithm.RS256)  // Dùng private key để ký
                .compact();
    }
}
