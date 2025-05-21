package org.dmdev.bookstore.dto;

import lombok.Builder;
import org.dmdev.bookstore.domain.Author;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record BookDTO(UUID id,
                      String title,
                      String isbn,
                      LocalDate publishedDate,
                      int pages,
                      Author author) {
}
