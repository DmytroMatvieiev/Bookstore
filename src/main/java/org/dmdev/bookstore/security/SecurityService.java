package org.dmdev.bookstore.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dmdev.bookstore.domain.User;
import org.dmdev.bookstore.exception.AuthException;
import org.dmdev.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Integer expirationInSeconds;
    @Value("${jwt.issuer}")
    private String issuer;

    private TokenDetails generateToken(User user) {
        Map<String, Object> claims = new HashMap<>() {{
            put("role", user.getRole());
            put("username", user.getUsername());
        }};
        return generateToken(claims, user.getId().toString());
    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject) {
        Long expiration = expirationInSeconds * 1000L;
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);

        return generateToken(expirationDate, claims, subject);
    }

    private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject) {
        Date createdDate = new Date();
        String token = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();

        return TokenDetails.builder()
                .token(token)
                .issuedAt(createdDate)
                .expiryDate(expirationDate)
                .build();
    }

    public Mono<TokenDetails> authenticate(final String username, final String password) {
        log.info("Authentication attempt for username: {}", username);
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    if (!user.isEnabled()) {
                        log.warn("Authentication failed: account disabled for username {}", username);
                        return Mono.error(new AuthException("Account disabled", "ACCOUNT DISABLED"));
                    }
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        log.warn("Authentication failed: bad credentials for username {}", username);
                        return Mono.error(new BadCredentialsException("Bad credentials"));
                    }
                    log.info("Authentication successful for username {}", username);
                    return Mono.just(generateToken(user).toBuilder()
                            .id(user.getId())
                            .build());
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Authentication failed: user not found with username {}", username);
                    return Mono.error(new UsernameNotFoundException(username));
                }));
    }
}
