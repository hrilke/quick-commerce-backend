package com.quickcommerce.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class JwtUtil {

    private final Key key;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-seconds:900}")
    private long expirationSeconds;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String subject, Collection<String> roles) {
        Instant now = Instant.now();
        JwtBuilder b = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expirationSeconds, ChronoUnit.SECONDS)))
                .claim("roles", roles)
                .signWith(key, SignatureAlgorithm.HS256);
        return b.compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Optional<String> getSubject(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            Object r = claims.get("roles");
            if (r instanceof List) {
                return (List<String>) r;
            }
            return Collections.emptyList();
        } catch (JwtException | IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
