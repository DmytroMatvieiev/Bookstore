package org.dmdev.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.Author;
import org.dmdev.bookstore.dto.AuthorDTO;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.service.AuthorService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    Mono<ResponseModel> findAll(){
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    Mono<ResponseModel> find(@PathVariable UUID id){
        return authorService.find(id);
    }

    @PutMapping
    Mono<ResponseModel> save(@RequestBody AuthorDTO authorDTO) {
        return authorService.save(authorDTO);
    }

    @PostMapping
    Mono<ResponseModel> update(@RequestBody AuthorDTO authorDTO) {
        return authorService.update(authorDTO);
    }

    @DeleteMapping("/{id}")
    Mono<ResponseModel> delete(@PathVariable UUID id) {
        return authorService.delete(id);
    }
}
