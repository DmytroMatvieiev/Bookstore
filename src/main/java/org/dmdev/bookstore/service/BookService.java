package org.dmdev.bookstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dmdev.bookstore.domain.BookFile;
import org.dmdev.bookstore.dto.BookDTO;
import org.dmdev.bookstore.domain.Book;
import org.dmdev.bookstore.dto.BookFileDTO;
import org.dmdev.bookstore.dto.GenreDTO;
import org.dmdev.bookstore.mapper.BookFileMapper;
import org.dmdev.bookstore.mapper.BookMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.AuthorRepository;
import org.dmdev.bookstore.repository.BookFileRepository;
import org.dmdev.bookstore.repository.BookRepository;
import org.dmdev.bookstore.repository.GenreRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookFileRepository bookFileRepository;
    private final BookMapper mapper;
    private final BookFileMapper bookFileMapper;



    public Mono<ResponseModel> save(BookDTO bookDTO) {
        if (bookDTO.id() != null) {
            log.warn("Attempted to save a book that already has an ID: {}", bookDTO.id());
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Book already exists")
                    .build());
        }
        log.info("Attempting to save book: {}", bookDTO.title());
        return authorRepository.findById(bookDTO.authorId())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Author with ID {} not found", bookDTO.authorId());
                    return Mono.error(new IllegalArgumentException("Author not found"));
                }))
                .flatMap(author ->
                        bookRepository.save(mapper.dtoToBook(bookDTO))
                                .doOnNext(saved -> log.info("Book '{}' saved with ID {}", saved.getTitle(), saved.getId()))
                                .flatMap(savedBook -> {
                                    List<UUID> genreIds = bookDTO.genres().stream()
                                            .map(GenreDTO::id)
                                            .toList();
                                    return genreRepository.saveBookGenres(savedBook.getId(), genreIds)
                                            .doOnSuccess(v -> log.info("Genres {} linked to book ID {}", genreIds, savedBook.getId()))
                                            .then(Mono.defer(() -> bookFileRepository.saveBookFiles(bookDTO.bookFiles()
                                                            .stream()
                                                            .map(dtos -> {
                                                                BookFile file = bookFileMapper.toBookFile(dtos);
                                                                file.setBookId(savedBook.getId());
                                                                return file;
                                                            })
                                                            .toList())
                                                    .doOnSuccess(v -> log.info("BookFiles linked to book ID {}", savedBook.getId()))
                                                    .thenReturn(ResponseModel.builder()
                                                            .status(ResponseModel.SUCCESS_STATUS)
                                                            .message("Book '%s' created".formatted(bookDTO.title()))
                                                            .build())));
                                }))
                .onErrorResume(ex -> {
                    log.error("Failed to save book '{}': {}", bookDTO.title(), ex.getMessage(), ex);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Failed to save book")
                            .build());
                });
    }

/*    public Mono<ResponseModel> addBookFile(BookFileDTO bookFileDTO) {
        log.info("Adding file for book: {}", bookFileDTO.bookId());
        return bookRepository.findById(bookFileDTO.bookId())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Book with ID {} not found", bookFileDTO.bookId());
                    return Mono.error(new IllegalArgumentException("Book not found"));
                }))
                .flatMap(book -> bookFileRepository.save(bookFileMapper.toBookFile(bookFileDTO))
                        .map(saved -> {
                            log.info("Book file saved: {}", saved.getId());
                            return ResponseModel.builder()
                                    .status(ResponseModel.SUCCESS_STATUS)
                                    .message("Book file added successfully")
                                    .build();
                        }))
                .onErrorResume(e -> {
                    log.error("Failed to add book file: {}", e.getMessage(), e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error: " + e.getMessage())
                            .build());
                });
    }*/

    public Mono<ResponseModel> delete(UUID id) {
        log.info("Attempting to delete book with ID {}", id);
        return bookRepository.findById(id)
                .flatMap(book -> bookRepository.delete(book)
                        .doOnSuccess(v -> log.info("Book with ID {} deleted successfully", id))
                        .thenReturn(ResponseModel.builder()
                                .status(ResponseModel.SUCCESS_STATUS)
                                .message("Book deleted successfully")
                                .build())
                )
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Book with ID {} not found", id);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Book not found")
                            .build());
                }))
                .onErrorResume(ex -> {
                    log.error("Failed to delete book with ID {}: {}", id, ex.getMessage(), ex);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Unexpected error: Internal server error")
                            .build());
                });
    }

    public Mono<ResponseModel> findAll(int limit, int size) {
        int offset = size * (limit - 1);
        log.info("Fetching books page {}, size {}, offset {}", limit, size, offset);
        return bookRepository.findAll(size, offset)  // assuming findAll(limit, offset) params order is (limit, offset)
                .map(mapper::bookToDto)
                .collectList()
                .map(bookList -> ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .message("Books retrieved successfully")
                        .data(bookList)
                        .build())
                .doOnError(ex -> log.error("Error retrieving books: {}", ex.getMessage()))
                .onErrorResume(ex -> Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Error retrieving books")
                        .build()));
    }

    public Mono<ResponseModel> findAllByAuthor(UUID authorId) {
        log.info("Finding books for author with id: {}", authorId);
        return authorRepository.findById(authorId)
                .flatMap(author -> bookRepository.findAllByAuthorId(authorId)
                        .map(mapper::bookToDto)
                        .collectList()
                        .map(bookList -> ResponseModel.builder()
                                .status(ResponseModel.SUCCESS_STATUS)
                                .message("Books by author retrieved successfully")
                                .data(bookList)
                                .build()))
                .switchIfEmpty(Mono.just(ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("Author not found")
                        .build()))
                .onErrorResume(ex -> {
                    log.error("Error finding books by author: {}", ex.getMessage());
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error finding books by author")
                            .build());
                });
    }

    public Mono<ResponseModel> findBooksByGenres(List<UUID> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            log.warn("findBooksByGenres called with empty or null genreIds");
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Genre list must not be empty")
                    .build());
        }
        log.info("Finding books for genres: {}", genreIds);
        return bookRepository.findBooksByGenres(genreIds, genreIds.size())
                .collectList()
                .flatMap(books -> {
                    if (books.isEmpty()) {
                        log.info("No books found for genres: {}", genreIds);
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.FAIL_STATUS)
                                .message("No books found for the given genres")
                                .build());
                    } else {
                        log.info("Found {} books for genres: {}", books.size(), genreIds);
                        var dtoList = books.stream().map(mapper::bookToDto).toList();
                        return Mono.just(ResponseModel.builder()
                                .status(ResponseModel.SUCCESS_STATUS)
                                .message("Books fetched successfully")
                                .data(dtoList)
                                .build());
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error finding books by genres: {}", genreIds, e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("rEror finding books by genres")
                            .build());
                });
    }

    public Mono<ResponseModel> findById(UUID id) {
        log.info("Finding book by id: {}", id);
        return bookRepository.findById(id)
                .flatMap(book -> {
                    log.info("Book found with id: {}", id);
                    return Mono.zip(genreRepository.findGenresByBookId(id).collectList(),
                                    bookFileRepository.findByBookId(id).collectList())
                            .map(tuple -> {
                                log.info("Genres: {}, Files: {}", tuple.getT1().size(), tuple.getT2().size());
                                return ResponseModel.builder()
                                        .status(ResponseModel.SUCCESS_STATUS)
                                        .message("Book with genres and files retrieved successfully")
                                        .data(mapper.bookDtoToSend(book, tuple.getT1(), tuple.getT2()))
                                        .build();
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Book not found with id: {}", id);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Book not found")
                            .build());
                }))
                .onErrorResume(e -> {
                    log.error("Error retrieving book with id {}: {}", id, e.getMessage(), e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error: " + e.getMessage())
                            .build());
                });
    }

    public Mono<ResponseModel> download(UUID bookFileId) {
        return bookFileRepository.findById(bookFileId)
                .flatMap(file -> {
                    Resource resource = new FileSystemResource(Paths.get(file.getFilePath()).toFile());
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.SUCCESS_STATUS)
                            .message("Downloading file")
                            .data(resource)
                            .build());
                })
                .onErrorResume(e -> {
                    log.error("Error downloading bookFile with id {}: {}", bookFileId, e.getMessage(), e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error: " + e.getMessage())
                            .build());
                });
    }

    public Mono<ResponseModel> update(BookDTO bookDTO) {
        log.info("Updating book: {}", bookDTO);
        if (bookDTO.id() == null) {
            log.warn("Book ID is null");
            return Mono.just(ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message("Book ID must not be null")
                    .build());
        }
        return bookRepository.findById(bookDTO.id())
                .flatMap(book -> {
                    log.info("Book found: {}", book.getId());
                    return bookRepository.save(mapper.dtoToBook(bookDTO))
                            .flatMap(savedBook -> {
                                // Update genres
                                List<UUID> genreIds = bookDTO.genres().stream()
                                        .map(GenreDTO::id)
                                        .toList();
                                return genreRepository.saveBookGenres(savedBook.getId(), genreIds)
                                        .thenReturn(ResponseModel.builder()
                                                .status(ResponseModel.SUCCESS_STATUS)
                                                .message("Book updated successfully")
                                                .data(mapper.bookToDto(savedBook))
                                                .build());
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Book not found with ID: {}", bookDTO.id());
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Book not found")
                            .build());
                }))
                .onErrorResume(e -> {
                    log.error("Error updating book: {}", e.getMessage(), e);
                    return Mono.just(ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Error: " + e.getMessage())
                            .build());
                });
    }
}
