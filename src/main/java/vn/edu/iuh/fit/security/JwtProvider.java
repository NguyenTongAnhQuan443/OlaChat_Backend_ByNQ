package vn.edu.iuh.fit.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.utils.KeyLoader;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    @Value("${jwt.access.expiration}")
    private long accessTokenValidity;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidity;

    private Key accessPrivateKey;
    private Key accessPublicKey;
    private Key refreshSecretKey;

    @PostConstruct
    public void init() throws Exception {
        this.accessPrivateKey = KeyLoader.loadPrivateKey("private.pem");
        this.accessPublicKey = KeyLoader.loadPublicKey("public.pem");
        this.refreshSecretKey = KeyLoader.loadRefreshSecretKey("refresh_secret.pem");
    }

    public String generateAccessToken(UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(accessPrivateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(refreshSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID extractUserId(String token, boolean isRefresh) {
        Claims claims = extractAllClaims(token, isRefresh);
        return UUID.fromString(claims.getSubject());
    }

    public boolean isTokenExpired(String token, boolean isRefresh) {
        return extractAllClaims(token, isRefresh).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token, boolean isRefresh) {
        JwtParser parser = Jwts.parser()
                .setSigningKey(isRefresh ? refreshSecretKey : accessPublicKey)
                .build();
        return parser.parseClaimsJws(token).getBody();
    }
}