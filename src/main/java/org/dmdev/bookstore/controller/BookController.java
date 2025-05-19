package org.dmdev.bookstore.controllers;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.models.ResponseModel;
import org.dmdev.bookstore.services.BookService;
import org.dmdev.bookstore.domain.Book;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    Mono<ResponseModel> save(@RequestBody Book book) {
        return bookService.save(book);
    }

    @GetMapping
    Mono<ResponseModel> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookService.findAll(size, page);
    }

    @GetMapping("/byAuthor/{authorId}")
    Mono<ResponseModel> findAllByAuthor(@PathVariable UUID authorId) {
        return bookService.findAllByAuthor(authorId);
    }

    @GetMapping("/{id}")
    Mono<ResponseModel> findById(@PathVariable UUID id) {
        return bookService.findById(id);
    }
}
