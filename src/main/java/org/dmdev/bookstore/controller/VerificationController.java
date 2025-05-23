package org.dmdev.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.User;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.security.verification.EmailTokenUtil;
import org.dmdev.bookstore.security.verification.VerificationService;
import org.dmdev.bookstore.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verify")
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping
    public Mono<ResponseModel> verifyEmail(@RequestParam("token") String token) {
        return verificationService.verify(token);
    }
}
