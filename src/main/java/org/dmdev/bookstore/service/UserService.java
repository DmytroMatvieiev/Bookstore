package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.User;
import org.dmdev.bookstore.domain.UserRole;
import org.dmdev.bookstore.dto.UserDTO;
import org.dmdev.bookstore.mapper.UserMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public Mono<ResponseModel> register(UserDTO userDTO) {
        return userRepository.save(mapper.dtoToUser(userDTO)).map(u ->
                ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message(String.format("User %s Created", userDTO.username()))
                        .build());
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



}
