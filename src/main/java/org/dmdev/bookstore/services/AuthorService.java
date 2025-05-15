package org.dmdev.bookstore.services;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.dtos.AuthorDTO;
import org.dmdev.bookstore.entities.Author;
import org.dmdev.bookstore.repositories.AuthorRepository;
import org.dmdev.bookstore.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public Mono<AuthorDTO> save(Author author) {
        if(author.getId() != null)
            return Mono.error(new IllegalArgumentException("Author already exists"));

        return authorRepository.save(author)
                .map(savedAuthor -> AuthorDTO.builder()
                        .id(savedAuthor.getId())
                        .firstName(savedAuthor.getFirstname())
                        .lastName(savedAuthor.getLastname())
                        .birthDate(savedAuthor.getBirthdate())
                        .deathDate(savedAuthor.getDateOfDeath())
                        .build());
    }


}
