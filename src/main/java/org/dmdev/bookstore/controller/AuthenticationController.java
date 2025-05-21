package org.dmdev.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.User;
import org.dmdev.bookstore.dto.AuthenticationRequestDTO;
import org.dmdev.bookstore.dto.AuthenticationResponseDTO;
import org.dmdev.bookstore.dto.UserDTO;
import org.dmdev.bookstore.model.ResponseModel;

import org.dmdev.bookstore.security.CustomPrincipal;
import org.dmdev.bookstore.security.SecurityService;
import org.dmdev.bookstore.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final SecurityService securityService;
    private final UserService userService;

    @PostMapping("/register")
    public Mono<ResponseModel> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    @PostMapping("/login")
    public Mono<AuthenticationResponseDTO> login(@RequestBody AuthenticationRequestDTO authDto) {
        return securityService.authenticate(authDto.username(), authDto.password())
                .flatMap(tokenDetails -> Mono.just(
                        AuthenticationResponseDTO.builder()
                                .id(tokenDetails.getId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssuedAt())
                                .expiresAt(tokenDetails.getExpiryDate())
                                .build()
                ));
    }

    @GetMapping("/info")
    public Mono<ResponseModel> getUserInfo(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.findById(customPrincipal.getId());
    }
}
