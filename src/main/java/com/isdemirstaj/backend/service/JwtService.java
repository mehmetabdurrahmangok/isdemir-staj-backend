package com.isdemirstaj.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Token imzalamak için kullanılan gizli anahtar (application.properties'den gelir)
    private final Key key;

    public JwtService(@Value("${jwt.secret}") String secretString) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
    }

    // Token Süreleri (İstediğiniz gibi ayarlandı):
    private static final long ACCESS_TOKEN_EXPIRATION = 5 * 60 * 1000; // 5 Dakika (milisaniye)
    private static final long REFRESH_TOKEN_EXPIRATION = 60 * 60 * 1000; // 1 Saat (milisaniye)

    // 1. ACCESS TOKEN ÜRETME (İçine e-posta ve rol saklanır)
    public String generateAccessToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", role); // Kullanıcının yetkisini token içine gömüyoruz

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. REFRESH TOKEN ÜRETME (Sadece e-posta ve uzun ömür saklanır)
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. TOKEN GEÇERLİLİK KONTROLÜ
    public boolean validateToken(String token) {
        try {
            // Eğer token başarıyla çözülebiliyorsa ve süresi geçmediyse true döner
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // 4. TOKEN İÇİNDEN E-POSTA ÇEKME
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 5. TOKEN İÇİNDEN ROL ÇEKME
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("rol", String.class);
    }

    // YARDIMCI METOTLAR:
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}