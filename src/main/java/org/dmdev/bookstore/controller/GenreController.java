package org.dmdev.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.Genre;
import org.dmdev.bookstore.dto.GenreDTO;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.service.GenreService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    Mono<ResponseModel> findAll(){
        return genreService.findAllGenres();
    }

    @PostMapping
    Mono<ResponseModel> save(@RequestBody GenreDTO genreDTO){
        return genreService.save(genreDTO);
    }

    @DeleteMapping("/{id}")
    Mono<ResponseModel> delete(@PathVariable UUID id){
        return genreService.delete(id);
    }

    @PutMapping
    Mono<ResponseModel> update(@RequestBody GenreDTO genreDTO){
        return genreService.update(genreDTO);
    }
}
