package org.dmdev.bookstore.repositories;

import org.dmdev.bookstore.entities.Author;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface AuthorRepository extends ReactiveCrudRepository<Author, UUID> {
}
