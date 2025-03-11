package vn.edu.iuh.fit.utils;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.constants.AuthConstants;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import io.jsonwebtoken.security.Keys;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final PrivateKey PRIVATE_KEY;
    private static final PublicKey PUBLIC_KEY;
    private static Key REFRESH_SECRET_KEY;

    static {
        try {
            PRIVATE_KEY = KeyLoader.loadPrivateKey("private.pem");
            PUBLIC_KEY = KeyLoader.loadPublicKey("public.pem");
            REFRESH_SECRET_KEY = KeyLoader.loadRefreshSecretKey("refresh_secret.pem");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tải khóa RSA", e);
        }
    }

    private final UserRepository userRepository;

    private Claims extractAllClaims(String token, boolean isRefreshToken) {
        try {
            JwtParserBuilder parserBuilder = Jwts.parser();
            if (isRefreshToken) {
                parserBuilder.setSigningKey(REFRESH_SECRET_KEY); // Dùng Secret Key cho HS256
            } else {
                parserBuilder.setSigningKey(PUBLIC_KEY); // Dùng Public Key cho RS256
            }
            return parserBuilder.build().parseClaimsJws(token).getBody();
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException(AuthConstants.MESSAGE_MALFORMED_JWT);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (SignatureException e) {
            System.out.println(AuthConstants.MESSAGE_INVALID_SIGNATURE + e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("JWT Token xử lý lỗi: " + e);
        }
    }

    public <T> T extractClaim(String token, boolean isRefreshToken, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token, isRefreshToken));
    }

    public Date extractExpiration(String token, boolean isRefreshToken) {
        return extractClaim(token, isRefreshToken, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token, boolean isRefreshToken) {
        return extractExpiration(token, isRefreshToken).before(new Date());
    }

    public boolean validateToken(String token, boolean isRefreshToken, UserDetails userDetails) {
        try {
            UUID userId = extractUserId(token, isRefreshToken);
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                return false;
            }

            return !isTokenExpired(token, isRefreshToken);
        } catch (Exception e) {
            return false;
        }
    }

    public String generateAccessToken(UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ
                .signWith(PRIVATE_KEY, SignatureAlgorithm.RS256) // Dùng RSA
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 10)) // 10 ngày
                .signWith(REFRESH_SECRET_KEY, SignatureAlgorithm.HS256) // Dùng HMAC SHA256
                .compact();
    }

    public UUID extractUserId(String token, boolean isRefreshToken) {
        try {
            String userIdString = extractAllClaims(token, isRefreshToken).getSubject();
            return UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            throw new MalformedJwtException("User ID trong token không hợp lệ" + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Không thể trích xuất User ID từ token" + e.getMessage());
        }
    }
}
