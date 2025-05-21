package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.dto.BookDTO;
import org.dmdev.bookstore.domain.Book;
import org.dmdev.bookstore.mapper.BookMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.AuthorRepository;
import org.dmdev.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repo;
    private final AuthorRepository authorRepository;
    private final BookMapper mapper;

    public Mono<ResponseModel> save(BookDTO bookDTO) {
        if (bookDTO.id() != null) {
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Book already exists")
                    .build());
        } else {
            return authorRepository.findById(bookDTO.authorId())
                    .flatMap(author -> repo.save(mapper.dtoToBook(bookDTO)))
                    .map(dto -> ResponseModel.builder()
                            .status(ResponseModel.SUCCESS_STATUS)
                            .message(String.format("Book %s Created", bookDTO.title()))
                            .build())
                    .switchIfEmpty(Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Author not found")
                            .build()))
                    .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Unexpected error: Iternal server error")
                            .build()));
        }
    }

    public Mono<ResponseModel> delete(UUID id) {
        return repo.findById(id)
                .flatMap(book -> repo.delete(book)
                        .then(Mono.just(ResponseModel.builder()
                                .status(ResponseModel.SUCCESS_STATUS)
                                .message("Book deleted successfully")
                                .build()))
                )
                .switchIfEmpty(Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Book not found")
                        .build()))
                .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Unexpected error: Iternal server error")
                        .build()));
    }

    public Mono<ResponseModel> findAll(int limit, int size) {
        int offset = limit * size;
        return repo.findAll(limit, offset)
                .map(mapper::bookToDto)
                .collectList()
                .map(bookList -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message("Books retrieved successfully")
                        .data(bookList)
                        .build()
                )
                .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Error: " + ex.getMessage())
                        .build()));
    }

    public Mono<ResponseModel> findAllByAuthor(UUID authorId) {
        return authorRepository.findById(authorId)
                .flatMap(author ->
                        repo.findAllByAuthorId(authorId)
                                .map(mapper::bookToDto)
                                .collectList()
                                .map(bookList -> ResponseModel.builder()
                                        .status(ResponseModel.SUCCESS_STATUS)
                                        .message("Books by author retrieved successfully")
                                        .data(bookList)
                                        .build())
                )
                .switchIfEmpty(Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Author not found")
                        .build()))
                .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Error: " + ex.getMessage())
                        .build()));
    }


    public Mono<ResponseModel> findById(UUID id) {
        return repo.findById(id)
                .map(mapper::bookToDto)
                .map(dto -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message("Book retrieved successfully")
                        .data(dto)
                        .build())
                .switchIfEmpty(Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Book or Author not found")
                        .build()))
                .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Error: " + ex.getMessage())
                        .build()));
    }

}
