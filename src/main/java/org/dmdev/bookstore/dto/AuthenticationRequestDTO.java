package org.dmdev.bookstore.dto;

import lombok.Builder;

@Builder
public record AuthenticationRequestDTO(
        String username,
        String password
) {
}
