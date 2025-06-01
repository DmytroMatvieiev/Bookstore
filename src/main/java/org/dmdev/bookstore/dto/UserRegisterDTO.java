package org.dmdev.bookstore.dto;

public record UserRegisterDTO(
        String username,
        String password,
        String email
) {
}
