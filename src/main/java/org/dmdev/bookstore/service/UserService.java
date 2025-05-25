package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dmdev.bookstore.domain.User;
import org.dmdev.bookstore.domain.UserRole;
import org.dmdev.bookstore.dto.UserDTO;
import org.dmdev.bookstore.mapper.UserMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.UserRepository;
import org.dmdev.bookstore.security.verification.EmailService;
import org.dmdev.bookstore.security.verification.EmailTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final EmailService emailService;
    private final EmailTokenUtil emailTokenUtil;

    public Mono<ResponseModel> register(UserDTO userDTO) {
        log.info("Attempting to register user with email: {}", userDTO.email());
        if (userDTO.id() != null) {
            log.warn("Registration failed: User {} already has an ID", userDTO.username());
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message(String.format("User %s exist", userDTO.username()))
                    .build());
        }
        return userRepository.findByEmail(userDTO.email())
                .flatMap(existingUser -> {
                    log.info("User with email {} already exists", userDTO.email());
                    if (existingUser.isVerified()) {
                        log.info("User {} is already verified", userDTO.username());
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.SUCCESS_STATUS)
                                .message(String.format("User %s exist", userDTO.username()))
                                .build());
                    } else {
                        log.warn("User {} exists but is not verified", userDTO.username());
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.FAIL_STATUS)
                                .message(String.format("User %s exist but is not verified", userDTO.username()))
                                .build());
                    }
                })
                .switchIfEmpty(Mono.defer(() -> {
                    User user = mapper.dtoToUser(userDTO);
                    user.setVerificationToken(emailTokenUtil.generateToken(user.getEmail()));
                    log.info("Registering new user: {}", userDTO.username());
                    return userRepository.save(user)
                            .flatMap(savedUser -> {
                                log.debug("Saved user {}, sending verification email...", savedUser.getEmail());
                                return emailService.sendVerificationEmail(
                                                savedUser.getEmail(), savedUser.getVerificationToken())
                                        .then(Mono.fromCallable(() -> {
                                            log.info("User {} registered successfully", savedUser.getUsername());
                                            return ResponseModel.builder()
                                                    .status(ResponseModel.SUCCESS_STATUS)
                                                    .message(String.format("User %s registered. Please, verify your email", userDTO.username()))
                                                    .build();
                                        }));
                            });
                }))
                .onErrorResume(ex -> {
                    log.error("Error during registration of user {}: {}", userDTO.username(), ex.getMessage(), ex);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error: " + ex.getMessage())
                            .build());
                });
    }

    public Mono<ResponseModel> findById(UUID id) {
        log.info("Searching for user with ID: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    log.info("User with ID {} found", id);
                    return ResponseModel.builder()
                            .status(ResponseModel.SUCCESS_STATUS)
                            .message("User found")
                            .data(mapper.userToDto(user))
                            .build();
                })
                .switchIfEmpty(Mono.fromCallable(() -> {
                    log.warn("User with ID {} not found", id);
                    return ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("User not found")
                            .build();
                }))
                .onErrorResume(ex -> {
                    log.error("Error finding user with ID {}: {}", id, ex.getMessage(), ex);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error finding user")
                            .build());
                });
    }

    public Mono<ResponseModel> findByUsername(String username) {
        log.info("Searching for user with username: {}", username);
        return userRepository.findByUsername(username)
                .map(user -> {
                    log.info("User with username '{}' found", username);
                    return ResponseModel.builder()
                            .status(ResponseModel.SUCCESS_STATUS)
                            .message("User found")
                            .data(mapper.userToDto(user))
                            .build();
                })
                .switchIfEmpty(Mono.fromCallable(() -> {
                    log.warn("User with username '{}' not found", username);
                    return ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("User not found")
                            .build();
                }))
                .onErrorResume(ex -> {
                    log.error("Error while finding user by username '{}': {}", username, ex.getMessage(), ex);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error: " + ex.getMessage())
                            .build());
                });
    }

    public Mono<ResponseModel> findByEmail(String email) {
        log.info("Searching for user with email: {}", email);
        return userRepository.findByEmail(email)
                .map(user -> {
                    log.info("User with email '{}' found", email);
                    return ResponseModel.builder()
                            .status(ResponseModel.SUCCESS_STATUS)
                            .message("User found")
                            .data(mapper.userToDto(user))
                            .build();
                })
                .switchIfEmpty(Mono.fromCallable(() -> {
                    log.warn("User with email '{}' not found", email);
                    return ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("User not found")
                            .build();
                }))
                .onErrorResume(ex -> {
                    log.error("Error while finding user by email '{}': {}", email, ex.getMessage(), ex);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error while finding user by email: " + email)
                            .build());
                });
    }


}
