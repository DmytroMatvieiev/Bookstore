package org.dmdev.bookstore.mapper.impl;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CustomGenreRepository {
    Mono<Void> saveBookGenres(UUID bookId, List<UUID> genreIds);
}
