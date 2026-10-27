package com.ikaansh.ems.security;

import com.ikaansh.ems.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final Key signingKey;
    private final long expirationMs;
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); this.expirationMs = expirationMs;
    }
    public String generateToken(AppUser user) {
        Date now = new Date();
        return Jwts.builder().subject(user.getEmail()).claim("uid", user.getId()).claim("role", user.getRole().name())
            .issuedAt(now).expiration(new Date(now.getTime() + expirationMs)).signWith(signingKey).compact();
    }
    public String extractEmail(String token) { return claims(token).getSubject(); }
    public boolean isValid(String token, String email) {
        try { return email.equalsIgnoreCase(extractEmail(token)) && claims(token).getExpiration().after(new Date()); }
        catch (RuntimeException ex) { return false; }
    }
    public long getExpirationSeconds() { return expirationMs / 1000; }
    private Claims claims(String token) { return Jwts.parser().verifyWith((javax.crypto.SecretKey) signingKey).build().parseSignedClaims(token).getPayload(); }
}
