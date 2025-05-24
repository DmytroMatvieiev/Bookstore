package org.dmdev.bookstore.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.BookGenre;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomGenreRepositoryImpl implements CustomGenreRepository {

    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Void> saveBookGenres(UUID bookId, List<UUID> genreIds) {
        return Flux.fromIterable(genreIds)
                .flatMap(genreId -> template.insert(BookGenre.class)
                        .using(new BookGenre(bookId, genreId))
                        .then()
                )
                .then();
    }
}
