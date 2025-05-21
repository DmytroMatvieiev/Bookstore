package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.dto.AuthorDTO;
import org.dmdev.bookstore.domain.Author;
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

    public Mono<ResponseModel> findAll() {
        return authorRepository.findAll()
                .map(author -> AuthorDTO.builder()
                        .id(author.getId())
                        .firstName(author.getFirstname())
                        .lastName(author.getLastname())
                        .birthDate(author.getBirthdate())
                        .deathDate(author.getDateOfDeath())
                        .build())
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
                .map(savedAuthor -> AuthorDTO.builder()
                        .id(savedAuthor.getId())
                        .firstName(savedAuthor.getFirstname())
                        .lastName(savedAuthor.getLastname())
                        .birthDate(savedAuthor.getBirthdate())
                        .deathDate(savedAuthor.getDateOfDeath())
                        .build())
                .map(dto -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message(String.format("Author %s %s saved successfully", dto.firstName(), dto.lastName()))
                        .data(dto)
                        .build())
                .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Error: " + ex.getMessage())
                        .build()));
    }



}
