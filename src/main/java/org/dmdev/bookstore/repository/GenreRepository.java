package org.dmdev.bookstore.repository;

import org.dmdev.bookstore.domain.Genre;
import org.dmdev.bookstore.repository.custom.CustomGenreRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface GenreRepository extends ReactiveCrudRepository<Genre, UUID>, CustomGenreRepository {

    @Query("""
    SELECT g.* FROM genres g
    JOIN book_genres bg ON g.id = bg.genre_id
    WHERE bg.book_id = :bookId
""")
    Flux<Genre> findGenresByBookId(UUID bookId);


}
