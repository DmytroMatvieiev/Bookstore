package org.dmdev.bookstore.security.verification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dmdev.bookstore.domain.User;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.UserRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationService {

    private final EmailTokenUtil emailTokenUtil;
    private final UserRepository userRepository;

    public Mono<ResponseModel> verify(String token) {
        String email = emailTokenUtil.extractEmail(token);
        log.info("Starting verification for email: {}", email);
        return userRepository.findByEmail(email)
                .flatMap(user -> {
                    if (!token.equals(user.getVerificationToken())) {
                        log.warn("Verification failed: token mismatch for email {}", email);
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.FAIL_STATUS)
                                .message("Token does not match.")
                                .build());
                    }
                    if (!emailTokenUtil.validateToken(token)) {
                        log.warn("Verification failed: token expired for email {}", email);
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.FAIL_STATUS)
                                .message("Token has expired.")
                                .build());
                    }
                    user.setVerified(true);
                    user.setVerificationToken(null);
                    return userRepository.save(user)
                            .doOnSuccess(savedUser -> log.info("Email verified successfully for user {}", email))
                            .map(savedUser -> ResponseModel.builder()
                                    .status(ResponseModel.SUCCESS_STATUS)
                                    .message("Email verified successfully.")
                                    .build());
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Verification failed: no user found for token {}", token);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Token expired.")
                            .build());
                }))
                .doOnError(e -> log.error("Error during verification for token {}: {}", token, e.getMessage(), e));
    }
 }
