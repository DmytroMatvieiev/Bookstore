package org.dmdev.bookstore.dto;

import lombok.Builder;

import java.util.Date;
import java.util.UUID;

@Builder
public record AuthenticationResponseDTO(
        UUID id,
        String token,
        Date issuedAt,
        Date expiresAt
) {
}
