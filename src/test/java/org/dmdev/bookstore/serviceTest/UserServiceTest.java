package org.dmdev.bookstore.serviceTest;

import org.dmdev.bookstore.domain.User;
import org.dmdev.bookstore.domain.UserRole;
import org.dmdev.bookstore.dto.UserRegisterDTO;
import org.dmdev.bookstore.mapper.UserMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.UserRepository;
import org.dmdev.bookstore.security.verification.EmailService;
import org.dmdev.bookstore.security.verification.EmailTokenUtil;
import org.dmdev.bookstore.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.hamcrest.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailTokenUtil emailTokenUtil;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;

    @Test
    void register_shouldRegisterNewUserSuccessfully() {
        // Given
        UserRegisterDTO userDTO = new UserRegisterDTO("newUser", "password123", "user@example.com");
        User mappedUser = new User();
        mappedUser.setUsername(userDTO.username());
        mappedUser.setEmail(userDTO.email());
        mappedUser.setPassword(userDTO.password()); // Normally hashed
        mappedUser.setRole(UserRole.ROLE_USER);
        mappedUser.setVerificationToken("mock-token");

        User savedUser = new User();
        savedUser.setUsername(mappedUser.getUsername());
        savedUser.setEmail(mappedUser.getEmail());
        savedUser.setVerificationToken(mappedUser.getVerificationToken());

        // When
        when(userRepository.findByEmail(userDTO.email())).thenReturn(Mono.empty());
        when(mapper.toRegisterUser(userDTO)).thenReturn(mappedUser);
        when(emailTokenUtil.generateToken(userDTO.email())).thenReturn("mock-token");
        when(userRepository.save(Mockito.<User>any())).thenReturn(Mono.just(savedUser));
        when(emailService.sendVerificationEmail(savedUser.getEmail(), "mock-token")).thenReturn(Mono.empty());

        // Then
        StepVerifier.create(userService.register(userDTO))
                .expectNextMatches(response ->
                        ResponseModel.SUCCESS_STATUS.equals(response.getStatus()) &&
                                response.getMessage().contains("registered"))
                .verifyComplete();

        verify(userRepository).findByEmail(userDTO.email());
        verify(userRepository).save(Mockito.<User>any());
        verify(emailService).sendVerificationEmail(savedUser.getEmail(), "mock-token");
    }
}
