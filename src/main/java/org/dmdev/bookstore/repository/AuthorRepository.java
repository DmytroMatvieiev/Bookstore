package org.dmdev.bookstore.repository;

import org.dmdev.bookstore.domain.Author;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface AuthorRepository extends ReactiveCrudRepository<Author, UUID> {
}
