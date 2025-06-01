package org.dmdev.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.service.GenreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Mono<ResponseModel> findAll(){
        return genreService.findAllGenres();
    }
}
