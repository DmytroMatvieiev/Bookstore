package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.Genre;
import org.dmdev.bookstore.dto.BookDTO;
import org.dmdev.bookstore.domain.Book;
import org.dmdev.bookstore.dto.GenreDTO;
import org.dmdev.bookstore.mapper.BookMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.AuthorRepository;
import org.dmdev.bookstore.repository.BookRepository;
import org.dmdev.bookstore.repository.GenreRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper mapper;
    private final GenreRepository genreRepository;

    public Mono<ResponseModel> save(BookDTO bookDTO) {
        if (bookDTO.id() != null) {
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Book already exists")
                    .build());
        }

        return authorRepository.findById(bookDTO.authorId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Author not found")))
                .flatMap(author -> {
                    Book book = mapper.dtoToBook(bookDTO);
                    return bookRepository.save(book)
                            .flatMap(savedBook -> {
                                List<UUID> genreIds = bookDTO.genres().stream()
                                        .map(GenreDTO::id)
                                        .toList();
                                return genreRepository.saveBookGenres(savedBook.getId(), genreIds)
                                        .thenReturn(ResponseModel.builder()
                                                .status(ResponseModel.SUCCESS_STATUS)
                                                .message("Book %s created".formatted(bookDTO.title()))
                                                .build());
                            });
                })
                .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Error: " + ex.getMessage())
                        .build()));
    }

    public Mono<ResponseModel> delete(UUID id) {
        return bookRepository.findById(id)
                .flatMap(book -> bookRepository.delete(book)
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
        return bookRepository.findAll(limit, offset)
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
                        bookRepository.findAllByAuthorId(authorId)
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

    public Mono<ResponseModel> findBooksByGenres(List<UUID> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Genre list must not be empty")
                    .build());
        }
        return bookRepository.findBooksByGenres(genreIds, genreIds.size())
                .collectList()
                .map(books -> books.stream().map(mapper::bookToDto).toList())
                .map(books -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message("Books fetched successfully")
                        .data(books)
                        .build())
                .defaultIfEmpty(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("No books found for the given genres")
                        .build())
                .onErrorResume(e -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Error: " + e.getMessage())
                        .build()));
    }

    public Mono<ResponseModel> findById(UUID id) {
        return bookRepository.findById(id)
                .flatMap(book -> genreRepository.findGenresByBookId(id)
                        .collectList()
                        .map(genres -> ResponseModel.builder()
                                .status(ResponseModel.SUCCESS_STATUS)
                                .message("Book with genres retrieved successfully")
                                .data(mapper.bookAndGenresToDto(book, genres))
                                .build()))
                .switchIfEmpty(Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Book not found")
                        .build()))
                .onErrorResume(e -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Error: " + e.getMessage())
                        .build()));
    }

}
