package com.stayeasy.api.security;

import com.stayeasy.api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component // บอก Spring ว่านี่คือ Bean
public class JwtTokenProvider {

    // 1. Secret Key (กุญแจลับ)
    // เราจะดึงค่านี้มาจาก application.properties

    @Value("${app.jwt.secret}")
    private String jwtSecretString;

    @Value("${app.jwt.expiration-in-ms}")
    private long jwtExpirationInMs;

    private SecretKey getSigningKey() {
        // สร้าง SecretKey จาก String ที่เรากำหนด (ต้องยาวพอสมควร)
        return Keys.hmacShaKeyFor(jwtSecretString.getBytes());
    }

    // 2. การสร้าง Token
    public String generateToken(Authentication authentication) {
        // ดึงข้อมูล User ที่ผ่านการ Login แล้ว
        User userDetails = (User) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // สร้าง JWT
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // เก็บ username (อีเมล)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // เข้ารหัสด้วย HS512
                .compact();
    }

    // 3. การตรวจสอบและอ่าน Token

    // ดึง username (อีเมล) ออกจาก Token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // ตรวจสอบว่า Token ถูกต้องและยังไม่หมดอายุ
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            // (ในระบบจริง ควร log exception ไว้)
            // เช่น MalformedJwtException, ExpiredJwtException, ...
            return false;
        }
    }
}