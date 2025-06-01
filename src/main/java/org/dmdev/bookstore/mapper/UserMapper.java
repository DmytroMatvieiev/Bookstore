package org.dmdev.bookstore.mapper;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.User;
import org.dmdev.bookstore.domain.UserRole;
import org.dmdev.bookstore.dto.UserDTO;
import org.dmdev.bookstore.dto.UserRegisterDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User dtoToUser(UserDTO dto){
        return User.builder()
                .username(dto.username())
                .password(passwordEncoder.encode(dto.password()))
                .email(dto.email())
                .role(dto.role())
                .verificationToken(dto.verificationToken())
                .isVerified(dto.isVerified())
                .resetToken(dto.resetToken())
                .enabled(true)
                .build();
    }

    public UserDTO userToDto(User user){
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .verificationToken(user.getVerificationToken())
                .isVerified(user.isVerified())
                .resetToken(user.getResetToken())
                .enabled(user.isEnabled())
                .build();
    }

    public User toRegisterUser(UserRegisterDTO dto){
        return User.builder()
                .username(dto.username())
                .password(passwordEncoder.encode(dto.password()))
                .email(dto.email())
                .build();
    }
}
