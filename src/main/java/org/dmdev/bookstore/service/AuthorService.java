package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dmdev.bookstore.dto.AuthorDTO;
import org.dmdev.bookstore.domain.Author;
import org.dmdev.bookstore.mapper.AuthorMapper;
import org.dmdev.bookstore.mapper.BookMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.AuthorRepository;
import org.dmdev.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public Mono<ResponseModel> findAll() {
        log.info("Find all authors");
        return authorRepository.findAll()
                .map(authorMapper::authorToDto)
                .collectList()
                .map(authorList -> {
                    log.info("Successfully retrieved {} authors", authorList.size());
                    return ResponseModel.builder()
                            .status(ResponseModel.SUCCESS_STATUS)
                            .message("Authors retrieved successfully")
                            .data(authorList)
                            .build();
                })
                .onErrorResume(ex -> {
                    log.error("Error while retrieving authors", ex);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error while retrieving authors")
                            .build());
                });
    }

    public Mono<ResponseModel> find(UUID id) {
        log.info("Finding author by id: {}", id);
        return authorRepository.findById(id)
                .map(author -> {
                    log.info("Author found: {}", author.getFirstname());
                    return ResponseModel.builder()
                            .status(ResponseModel.SUCCESS_STATUS)
                            .message("Author found")
                            .data(authorMapper.authorToDto(author))
                            .build();
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Author not found with id: {}", id);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Author not found")
                            .build());
                }))
                .onErrorResume(e -> {
                    log.error("Error retrieving author with id {}: {}", id, e.getMessage(), e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error: " + e.getMessage())
                            .build());
                });
    }

    public Mono<ResponseModel> save(AuthorDTO authorDto) {
        if (authorDto.id() != null) {
            log.warn("Attempted to save an author that already has an ID: {}", authorDto.id());
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Author already exists")
                    .build());
        }
        log.info("Saving new author: {} {}", authorDto.firstName(), authorDto.lastName());
        return authorRepository.save(authorMapper.dtoToAuthor(authorDto))
                .doOnNext(saved -> log.debug("Author saved with ID: {}", saved.getId()))
                .map(dto -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message(String.format("Author %s %s saved successfully", authorDto.firstName(), authorDto.lastName()))
                        .data(dto)
                        .build())
                .onErrorResume(ex -> {
                    log.error("Failed to save author", ex);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Failed to save author")
                            .build());
                });
    }

    public Mono<ResponseModel> delete(UUID id) {
        log.info("Deleting author by id: {}", id);
        return authorRepository.findById(id)
                .flatMap(author -> {
                    log.info("Author found: {}. Proceeding to delete.", author.getFirstname());
                    return authorRepository.delete(author)
                            .then(Mono.just(ResponseModel.builder()
                                    .status(ResponseModel.SUCCESS_STATUS)
                                    .message("Author deleted successfully")
                                    .build()));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Author not found with id: {}", id);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Author not found")
                            .build());
                }))
                .onErrorResume(e -> {
                    log.error("Error deleting author with id {}: {}", id, e.getMessage(), e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error: " + e.getMessage())
                            .build());
                });
    }

    public Mono<ResponseModel> update(AuthorDTO authorDto) {
        log.info("Updating author with ID: {}", authorDto.id());
        if (authorDto.id() == null) {
            log.warn("Author ID is null");
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Author ID must not be null")
                    .build());
        }
        return authorRepository.findById(authorDto.id())
                .flatMap(author -> {
                    log.info("Author found: {}", author.getFirstname());
                    return authorRepository.save(authorMapper.dtoToAuthor(authorDto))
                            .map(savedAuthor -> {
                                log.info("Author updated successfully: {}", savedAuthor.getId());
                                return ResponseModel.builder()
                                        .status(ResponseModel.SUCCESS_STATUS)
                                        .message("Author updated successfully")
                                        .data(authorMapper.authorToDto(savedAuthor))
                                        .build();
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Author not found with ID: {}", authorDto.id());
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Author not found")
                            .build());
                }))
                .onErrorResume(e -> {
                    log.error("Error updating author with ID {}: {}", authorDto.id(), e.getMessage(), e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Internal server error")
                            .build());
                });
    }
}
