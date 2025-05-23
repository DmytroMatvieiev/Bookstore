package org.dmdev.bookstore.security.verification;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.User;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.UserRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class VerificationService {

    private final EmailTokenUtil emailTokenUtil;
    private final UserRepository userRepository;

    public Mono<ResponseModel> verify(String token) {
        return userRepository.findByEmail(emailTokenUtil.extractEmail(token))
                .flatMap(user -> {
                    if (!token.equals(user.getVerificationToken())) {
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.FAIL_STATUS)
                                .message("Token does not match.")
                                .build());
                    }
                    if (!emailTokenUtil.validateToken(token)) {
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.FAIL_STATUS)
                                .message("Token has expired.")
                                .build());
                    }
                    user.setVerified(true);
                    user.setVerificationToken(null);
                    return userRepository.save(user)
                            .map(savedUser -> ResponseModel.builder()
                                    .status(ResponseModel.SUCCESS_STATUS)
                                    .message("Email verified successfully.")
                                    .build());
                })
                .switchIfEmpty(Mono.just(ResponseModel.builder()
                .status(ResponseModel.FAIL_STATUS)
                .message("Token expired.")
                .build()));
    }
 }
