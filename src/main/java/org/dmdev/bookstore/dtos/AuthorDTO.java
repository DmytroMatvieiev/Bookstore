package org.dmdev.bookstore.dtos;

import lombok.Builder;
import org.dmdev.bookstore.entities.Book;

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
