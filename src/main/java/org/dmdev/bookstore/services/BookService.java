package org.dmdev.bookstore.services;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.dtos.BookDTO;
import org.dmdev.bookstore.entities.Book;
import org.dmdev.bookstore.repositories.AuthorRepository;
import org.dmdev.bookstore.repositories.BookRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repo;
    private final AuthorRepository authorRepository;

    public Mono<BookDTO> save(Book book) {
        if(book.getUUID() != null)
            return Mono.error(new IllegalArgumentException("Book already exists"));

        return authorRepository.findById(book.getAuthor())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Author not found")))
                .flatMap(author ->
                        repo.save(book)
                                .map(newBook -> BookDTO.builder()
                                        .id(newBook.getUUID())
                                        .title(newBook.getTitle())
                                        .isbn(newBook.getISBN())
                                        .publishedDate(newBook.getPublicationDate())
                                        .pages(newBook.getPages())
                                        .author(author)
                                        .build())
                );
    }

    public Flux<BookDTO> findAll() {
        return repo.findAll()
                .flatMap(book ->
                        authorRepository.findById(book.getAuthor())
                                .map(author -> BookDTO.builder()
                                        .id(book.getUUID())
                                        .title(book.getTitle())
                                        .isbn(book.getISBN())
                                        .publishedDate(book.getPublicationDate())
                                        .pages(book.getPages())
                                        .author(author)
                                        .build()
                                )
                );
    }

    public Flux<BookDTO> findByAuthor(UUID authorId) {
        return authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Author not found")))
                .flatMapMany(author ->
                        repo.findAllByAuthor(authorId)
                                .map(book -> BookDTO.builder()
                                        .id(book.getUUID())
                                        .title(book.getTitle())
                                        .isbn(book.getISBN())
                                        .publishedDate(book.getPublicationDate())
                                        .pages(book.getPages())
                                        .author(author)
                                        .build())
                );
    }


    public Mono<BookDTO> findById(UUID id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Book not found")))
                .flatMap(book ->
                        authorRepository.findById(book.getAuthor())
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("Author not found")))
                                .map(author -> BookDTO.builder()
                                        .id(book.getUUID())
                                        .title(book.getTitle())
                                        .isbn(book.getISBN())
                                        .publishedDate(book.getPublicationDate())
                                        .pages(book.getPages())
                                        .author(author) // or author object if needed
                                        .build()
                                )
                );
    }
}
