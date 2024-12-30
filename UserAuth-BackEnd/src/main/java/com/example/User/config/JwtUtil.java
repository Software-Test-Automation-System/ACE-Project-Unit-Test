package com.example.User.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String key = "thisIsAStaticSecretKeyForJwtGeneration";
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(key.getBytes());
    private static final String refreshKey = "thisIsAStaticSecretKeyForJwtGeneration";
    private final Key REFRESH_SECRET_KEY = Keys.hmacShaKeyFor(refreshKey.getBytes());

    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(REFRESH_SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token, boolean isRefreshToken) {
        Key key = isRefreshToken ? REFRESH_SECRET_KEY : SECRET_KEY;
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, boolean isRefreshToken) {
        Key key = isRefreshToken ? REFRESH_SECRET_KEY : SECRET_KEY;
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // Parse and validate token
            return !isTokenExpired(token, isRefreshToken);
        } catch (Exception e) {
            return false;
        }
    }

    public Key getSecretKey(boolean isRefreshToken) {
        return isRefreshToken ? REFRESH_SECRET_KEY : SECRET_KEY;
    }

    private boolean isTokenExpired(String token, boolean isRefreshToken) {
        Key key = isRefreshToken ? REFRESH_SECRET_KEY : SECRET_KEY;
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }


}