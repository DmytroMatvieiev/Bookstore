package org.dmdev.bookstore.controllers;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.Author;
import org.dmdev.bookstore.models.ResponseModel;
import org.dmdev.bookstore.services.AuthorService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    Mono<ResponseModel> findAll(){
        return authorService.findAll();
    }

    @PostMapping
    Mono<ResponseModel> save(@RequestBody Author author) {
        return authorService.save(author);
    }
}
