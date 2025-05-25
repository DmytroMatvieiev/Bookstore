package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dmdev.bookstore.mapper.GenreMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.GenreRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public Mono<ResponseModel> findAllGenres() {
        log.info("Fetching all genres...");
        return genreRepository.findAll()
                .doOnNext(genreMapper::genreToDto)
                .collectList()
                .doOnSuccess(list -> log.info("Successfully retrieved {} genres", list.size()))
                .map(genres -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message("Genres retrieved successfully")
                        .data(genres)
                        .build())
                .onErrorResume(ex -> {
                    log.error("Error while fetching genres", ex);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error while fetching genres")
                            .build());
                });
    }

    public Mono<ResponseModel> findGenresByBookId(UUID bookId) {
        log.info("Finding genres for book with ID: {}", bookId);
        return genreRepository.findGenresByBookId(bookId)
                .collectList()
                .flatMap(genres -> {
                    if (genres.isEmpty()) {
                        log.warn("No genres found for book with ID: {}", bookId);
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.FAIL_STATUS)
                                .message("No genres found for this book")
                                .build());
                    } else {
                        log.info("Found {} genres for book with ID: {}", genres.size(), bookId);
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.SUCCESS_STATUS)
                                .message("Genres retrieved successfully")
                                .data(genres)
                                .build());
                    }
                })
                .onErrorResume(ex -> {
                    log.error("Error retrieving genres for book ID: {}", bookId, ex);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error retrieving genres for book")
                            .build());
                });
    }

}
