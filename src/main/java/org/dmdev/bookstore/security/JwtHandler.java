package org.dmdev.bookstore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.exception.AuthException;
import org.dmdev.bookstore.exception.UnauthorizedException;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Date;

@AllArgsConstructor
public class JwtHandler {

    private final String secret;

    public Mono<VerificationResult> checkToken(String token) {
        return Mono.just(verifyToken(token))
                .onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
    }

    private VerificationResult verifyToken(String token) {
        Claims claims = getClaimsFromToken(token);
        final Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            throw new AuthException("Expired or invalid JWT token", "Expired or invalid JWT token");
        }
        return new VerificationResult(claims, token);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                .parseClaimsJws(token)
                .getBody();
    }

    @AllArgsConstructor
    public static class VerificationResult{
        public Claims claims;
        public String token;
    }

}
