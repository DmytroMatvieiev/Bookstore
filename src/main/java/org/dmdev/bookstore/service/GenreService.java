package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.GenreRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public Mono<ResponseModel> findGenresByBookId(UUID bookId) {
        return genreRepository.findGenresByBookId(bookId)
                .collectList()
                .map(genres -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message("Genres retrieved successfully")
                        .data(genres)
                        .build())
                .switchIfEmpty(Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("No genres found for this book")
                        .build()));
    }
}
