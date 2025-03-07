package vn.edu.iuh.fit.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final PrivateKey PRIVATE_KEY;
    private static final PublicKey PUBLIC_KEY;

    static {
        try {
            PRIVATE_KEY = KeyLoader.loadPrivateKey("private.pem");
            PUBLIC_KEY = KeyLoader.loadPublicKey("public.pem");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tải khóa RSA", e);
        }
    }

    private final UserRepository userRepository;

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(PUBLIC_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
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
        return extractClaim(token, Claims::getSubject).equals(((User) userDetails).getId().toString())
                && !isTokenExpired(token);
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ
                .signWith(PRIVATE_KEY, SignatureAlgorithm.RS256)
                .compact();
    }
}
