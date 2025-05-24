package org.dmdev.bookstore.mapper;

import org.dmdev.bookstore.domain.Genre;
import org.dmdev.bookstore.dto.GenreDTO;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public Genre dtoToGenre(GenreDTO dto) {
        return Genre.builder()
                .id(dto.id())
                .name(dto.name())
                .build();
    }

    public GenreDTO genreToDto(Genre genre) {
        return GenreDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}
