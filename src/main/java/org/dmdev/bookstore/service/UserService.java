package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final EmailService emailService;
    private final EmailTokenUtil emailTokenUtil;

    public Mono<ResponseModel> register(UserDTO userDTO) {
        if (userDTO.id() != null) {
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message(String.format("User %s exist", userDTO.username()))
                    .build());
        }
        return userRepository.findByEmail(userDTO.email())
                .flatMap(existingUser -> {
                    if (existingUser.isVerified()) {
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.SUCCESS_STATUS)
                                .message(String.format("User %s exist", userDTO.username()))
                                .build());
                    } else {
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.FAIL_STATUS)
                                .message(String.format("User %s exist but is not verified", userDTO.username()))
                                .build());
                    }
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                                    User user = mapper.dtoToUser(userDTO);
                                    user.setVerificationToken(emailTokenUtil.generateToken(user.getEmail()));
                                    return userRepository.save(user)
                                            .flatMap(savedUser ->
                                                    emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getVerificationToken())
                                                            .then(Mono.fromCallable(() -> ResponseModel.builder()
                                                                    .status(ResponseModel.SUCCESS_STATUS)
                                                                    .message(String.format("User %s registered. Please, verify your email", userDTO.username()))
                                                                    .build())));
                                }
                        ));
    }

    public Mono<ResponseModel> findById(UUID id) {
        return userRepository.findById(id)
                .map(u ->
                        ResponseModel.builder()
                                .message("User found")
                                .data(mapper.userToDto(u))
                                .build());
    }

    public Mono<ResponseModel> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(u -> ResponseModel.builder()
                        .message("User found")
                        .data(mapper.userToDto(u))
                        .build());
    }

    public Mono<ResponseModel> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(u -> ResponseModel.builder()
                        .message("User found")
                        .data(mapper.userToDto(u))
                        .build());
    }


}
