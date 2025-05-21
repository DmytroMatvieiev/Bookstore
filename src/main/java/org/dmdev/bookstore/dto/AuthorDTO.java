package org.dmdev.bookstore.dto;

import lombok.Builder;
import org.dmdev.bookstore.domain.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record AuthorDTO(UUID id,
                        String firstName,
                        String lastName,
                        LocalDate birthDate,
                        LocalDate deathDate,
                        List<Book> books) {
}
