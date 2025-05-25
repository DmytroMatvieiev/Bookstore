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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

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


    public Mono<ResponseModel> save(Author author) {
        if (author.getId() != null) {
            log.warn("Attempted to save an author that already has an ID: {}", author.getId());
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Author already exists")
                    .build());
        }
        log.info("Saving new author: {} {}", author.getFirstname(), author.getLastname());
        return authorRepository.save(author)
                .doOnNext(saved -> log.debug("Author saved with ID: {}", saved.getId()))
                .map(dto -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message(String.format("Author %s %s saved successfully", author.getFirstname(), author.getLastname()))
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




}
