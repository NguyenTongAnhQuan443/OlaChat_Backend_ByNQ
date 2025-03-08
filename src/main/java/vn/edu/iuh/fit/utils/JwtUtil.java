package vn.edu.iuh.fit.utils;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.constants.AuthConstants;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
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
        try {
            return Jwts.parser()
                    .setSigningKey(PUBLIC_KEY)  // Dùng public key để xác minh
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
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
        try {
            UUID userId = extractUserId(token);
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                System.out.println("User not found for ID: " + userId);
                return false;
            }

            String usernameFromToken = userOpt.get().getEmail() != null ? userOpt.get().getEmail() : userOpt.get().getPhoneNumber();
            String usernameFromUserDetails = userDetails.getUsername();

            if (!usernameFromToken.equals(usernameFromUserDetails)) {
                System.out.println("Username from token does not match UserDetails: " + usernameFromToken + " vs " + usernameFromUserDetails);
                return false;
            }

            return !isTokenExpired(token);
        } catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage());
            return false;
        }
    }

    public String generateToken(UUID userId) {
        return Jwts.builder().setSubject(userId.toString()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ
                .signWith(PRIVATE_KEY, SignatureAlgorithm.RS256).compact();
    }

    public UUID extractUserId(String token) {
        try {
            String userIdString = extractClaim(token, Claims::getSubject);
            return UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            throw new MalformedJwtException("User ID trong token không hợp lệ" + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Không thể trích xuất User ID từ token" + e.getMessage());
        }
    }
}
