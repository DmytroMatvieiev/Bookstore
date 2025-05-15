package org.dmdev.bookstore.dtos;

import lombok.Builder;
import org.dmdev.bookstore.entities.Author;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record BookDTO(UUID id,
                      String title,
                      String isbn,
                      LocalDate publishedDate,
                      int pages,
                      Author author) {
}
