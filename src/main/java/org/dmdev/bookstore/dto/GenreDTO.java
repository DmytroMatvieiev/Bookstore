package org.dmdev.bookstore.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GenreDTO (
        UUID id,
        String name
) {
}
