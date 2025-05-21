package org.dmdev.bookstore.mapper;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.Author;
import org.dmdev.bookstore.dto.AuthorDTO;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorMapper {

    private BookMapper bookMapper;

    public Author dtoToAuthor(AuthorDTO dto) {
        return Author.builder()
                .id(dto.id())
                .firstname(dto.firstName())
                .lastname(dto.lastName())
                .birthdate(dto.birthDate())
                .dateOfDeath(dto.deathDate())
                .build();
    }

    public AuthorDTO authorToDto(Author author) {
        return AuthorDTO.builder()
                .id(author.getId())
                .firstName(author.getFirstname())
                .lastName(author.getLastname())
                .birthDate(author.getBirthdate())
                .deathDate(author.getDateOfDeath())
                .build();
    }
}
