package org.dmdev.bookstore.security.verification;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class EmailTokenUtil {

    @Value("${jwt.emailSecret}")
    private String emailSecret;
    @Value("${jwt.emailExpiration}")
    private long EXPIRATION_TIME;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(emailSecret.getBytes()))
                .compact();
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public String extractEmail(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(Base64.getEncoder().encodeToString(emailSecret.getBytes()))
                .build();
        return parser.parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    private Date extractExpirationDate(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(Base64.getEncoder().encodeToString(emailSecret.getBytes()))
                .build();
        return parser.parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
