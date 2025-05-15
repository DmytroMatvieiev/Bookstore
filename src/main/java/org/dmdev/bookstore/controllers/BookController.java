package org.dmdev.bookstore.controllers;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.dtos.BookDTO;
import org.dmdev.bookstore.services.BookService;
import org.dmdev.bookstore.entities.Book;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    Mono<BookDTO> save(@RequestBody Book book) {
        return bookService.save(book);
    }

    @GetMapping
    Flux<BookDTO> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/byAuthor/{authorId}")
    Flux<BookDTO> findAllByAuthor(@PathVariable UUID authorId) {
        return bookService.findByAuthor(authorId);
    }

    @GetMapping("/{id}")
    Mono<BookDTO> findById(@PathVariable UUID id) {
        return bookService.findById(id);
    }
}
