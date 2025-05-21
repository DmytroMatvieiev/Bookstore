package org.dmdev.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.dmdev.bookstore.domain.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserDTO(
        UUID id,
        String username,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String email,
        UserRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean enabled
) {
}
