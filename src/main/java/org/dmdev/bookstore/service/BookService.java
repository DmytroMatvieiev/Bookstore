package org.dmdev.bookstore.services;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.dtos.BookDTO;
import org.dmdev.bookstore.domain.Book;
import org.dmdev.bookstore.models.ResponseModel;
import org.dmdev.bookstore.repositories.AuthorRepository;
import org.dmdev.bookstore.repositories.BookRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repo;
    private final AuthorRepository authorRepository;

    public Mono<ResponseModel> save(Book book) {
        if (book.getUUID() != null) {
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Book already exists")
                    .build());
        } else {
            return authorRepository.findById(book.getAuthor())
                    .flatMap(author -> repo.save(book)
                            .map(newBook -> BookDTO.builder()
                                    .id(newBook.getUUID())
                                    .title(newBook.getTitle())
                                    .isbn(newBook.getISBN())
                                    .publishedDate(newBook.getPublicationDate())
                                    .pages(newBook.getPages())
                                    .author(author)
                                    .build()))
                    .map(dto -> ResponseModel.builder()
                            .status(ResponseModel.SUCCESS_STATUS)
                            .message(String.format("Book %s Created", dto.title()))
                            .build())
                    .switchIfEmpty(Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Author not found")
                            .build()))
                    .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Unexpected error: Iternal error")
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
                        .message("Error: " + ex.getMessage())
                        .build()));
    }

    public Mono<ResponseModel> findAll(int limit, int size) {
        int offset = limit * size;
        return repo.findAll(limit, offset)
                .flatMap(book ->
                        authorRepository.findById(book.getAuthor())
                                .map(author -> BookDTO.builder()
                                        .id(book.getUUID())
                                        .title(book.getTitle())
                                        .isbn(book.getISBN())
                                        .publishedDate(book.getPublicationDate())
                                        .pages(book.getPages())
                                        .author(author)
                                        .build())
                )
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
                        repo.findAllByAuthor(authorId)
                                .map(book -> BookDTO.builder()
                                        .id(book.getUUID())
                                        .title(book.getTitle())
                                        .isbn(book.getISBN())
                                        .publishedDate(book.getPublicationDate())
                                        .pages(book.getPages())
                                        .author(author)
                                        .build())
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
                .flatMap(book -> authorRepository.findById(book.getAuthor())
                        .map(author -> BookDTO.builder()
                                .id(book.getUUID())
                                .title(book.getTitle())
                                .isbn(book.getISBN())
                                .publishedDate(book.getPublicationDate())
                                .pages(book.getPages())
                                .author(author)
                                .build())
                )
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
