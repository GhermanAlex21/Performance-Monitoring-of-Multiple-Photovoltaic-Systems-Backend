package com.example.springboot.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {
    private static final long EXPIRATION_TIME = 900_000;  // 15 minutes
    private static final String SECRET_KEY_STR = "aW4gYSBzZWNyZXQga2V5IHN0cmluZyB3aGljaCBtdXN0IGJlIGF0IGxlYXN0IDMyIGJ5dGVzIGxvbmcgd2l0aCBhIGJpZyBlbm91Z2ggZW50cm9weQ==";  // 'Some$ecretKey' encoded in Base64

    public static byte[] getSecretKey() {
        // Decode Base64 String to bytes
        return Base64.getDecoder().decode(SECRET_KEY_STR);
    }

    public static String generateToken(String username, String role, Integer userId) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("YourApp")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(getSecretKey()), SignatureAlgorithm.HS256) // Use direct byte[] key
                .compact();
    }
}