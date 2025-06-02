package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dmdev.bookstore.dto.GenreDTO;
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

    public Mono<ResponseModel> save(GenreDTO genreDTO) {
        log.info("Saving genre: {}", genreDTO);
        if (genreDTO.id() != null) {
            log.warn("Attempt to save genre with existing ID: {}", genreDTO.id());
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Genre already exists")
                    .build());
        }
        return genreRepository.save(genreMapper.dtoToGenre(genreDTO))
                .map(savedGenre -> {
                    log.info("Genre saved with ID: {}", savedGenre.getId());
                    return ResponseModel.builder()
                            .status(ResponseModel.SUCCESS_STATUS)
                            .message("Genre saved successfully")
                            .build();
                })
                .onErrorResume(e -> {
                    log.error("Error saving genre: {}", e.getMessage(), e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error saving genre")
                            .build());
                });
    }

    public Mono<ResponseModel> findAllGenres() {
        log.info("Fetching all genres...");
        return genreRepository.findAll()
                .map(genreMapper::genreToDto)
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

    public Mono<ResponseModel> delete(UUID genreId) {
        log.info("Deleting genre with ID: {}", genreId);
        return genreRepository.findById(genreId)
                .flatMap(genre -> genreRepository.delete(genre)
                        .thenReturn(ResponseModel.builder()
                                .status(ResponseModel.SUCCESS_STATUS)
                                .message("Genre deleted successfully")
                                .build())
                )
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Genre not found with ID: {}", genreId);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Genre not found")
                            .build());
                }))
                .onErrorResume(e -> {
                    log.error("Error deleting genre with ID {}: {}", genreId, e.getMessage(), e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error deleting genre")
                            .build());
                });
    }

    public Mono<ResponseModel> update(GenreDTO genreDTO) {
        log.info("Updating genre: {}", genreDTO);
        if (genreDTO.id() == null) {
            log.warn("Genre ID is null");
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Genre ID must not be null")
                    .build());
        }
        return genreRepository.findById(genreDTO.id())
                .flatMap(existingGenre -> genreRepository.save(genreMapper.dtoToGenre(genreDTO))
                        .map(saved -> {
                            log.info("Genre updated successfully: {}", saved.getId());
                            return ResponseModel.builder()
                                    .status(ResponseModel.SUCCESS_STATUS)
                                    .message("Genre updated successfully")
                                    .build();
                        })
                )
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Genre not found with ID: {}", genreDTO.id());
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Genre not found")
                            .build());
                }))
                .onErrorResume(e -> {
                    log.error("Error updating genre {}: {}", genreDTO.id(), e.getMessage(), e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error: " + e.getMessage())
                            .build());
                });
    }

}
