package org.dmdev.bookstore.repository.custom;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CustomGenreRepository {
    Mono<Void> saveBookGenres(UUID bookId, List<UUID> genreIds);
}
