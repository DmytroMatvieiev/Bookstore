package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.dto.AuthorDTO;
import org.dmdev.bookstore.domain.Author;
import org.dmdev.bookstore.mapper.AuthorMapper;
import org.dmdev.bookstore.mapper.BookMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.AuthorRepository;
import org.dmdev.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

    public Mono<ResponseModel> findAll() {
        return authorRepository.findAll()
                .map(authorMapper::authorToDto)
                .collectList()
                .map(authorList -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message("Authors retrieved successfully")
                        .data(authorList)
                        .build())
                .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Error: " + ex.getMessage())
                        .build()));
    }


    public Mono<ResponseModel> save(Author author) {
        if (author.getId() != null) {
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Author already exists")
                    .build());
        }
        return authorRepository.save(author)
                .map(dto -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message(String.format("Author %s %s saved successfully", author.getFirstname(), author.getLastname()))
                        .data(dto)
                        .build())
                .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Error: " + ex.getMessage())
                        .build()));
    }



}
