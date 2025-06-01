package org.dmdev.bookstore.dto;
import org.springframework.core.io.Resource;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BookFileDTO(
        UUID id,
        UUID bookId,
        String format,
        String filepath
) {
}
