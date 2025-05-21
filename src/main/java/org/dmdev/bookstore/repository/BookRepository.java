package org.dmdev.bookstore.repository;

import org.dmdev.bookstore.domain.Book;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface BookRepository extends ReactiveCrudRepository<Book, UUID> {

    @Query("SELECT * FROM books ORDER BY publication_date ASC LIMIT :limit OFFSET :offset")
    Flux<Book> findAll(int limit, int offset);

    Flux<Book> findAllByAuthor(UUID author);
}
