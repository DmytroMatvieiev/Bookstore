package org.dmdev.bookstore.repositories;

import org.dmdev.bookstore.entities.Book;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BookRepository extends ReactiveCrudRepository<Book, UUID> {

    Flux<Book> findAllByAuthor(UUID author);
}
