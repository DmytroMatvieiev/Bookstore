package org.dmdev.bookstore.controllers;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.dtos.AuthorDTO;
import org.dmdev.bookstore.entities.Author;
import org.dmdev.bookstore.services.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public Mono<AuthorDTO> save(@RequestBody Author author) {
        return authorService.save(author);
    }
}
