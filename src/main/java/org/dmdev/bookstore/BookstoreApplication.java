package org.dmdev.bookstore;

import org.dmdev.bookstore.dtos.AuthorDTO;
import org.dmdev.bookstore.entities.Author;
import org.dmdev.bookstore.entities.Book;
import org.dmdev.bookstore.services.AuthorService;
import org.dmdev.bookstore.services.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@SpringBootApplication
public class BookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(BookService bookService, AuthorService authorService) {
        return args -> {
            Flux<AuthorDTO> authorFlux = Flux.range(0, 3)
                    .map(i -> Author.builder()
                            .firstname("firstname" + i)
                            .lastname("lastname" + i)
                            .birthdate(LocalDate.now())
                            .build())
                    .flatMap(authorService::save)
                    .cache();

            authorFlux.collectList().flatMapMany(authors -> {
                return Flux.range(0, 10)
                        .map(i -> {
                            AuthorDTO randomAuthor = authors.get(i % authors.size()); // Cycle through authors
                            return Book.builder()
                                    .title("book " + i)
                                    .ISBN("ISBN " + i)
                                    .author(randomAuthor.id())
                                    .build();
                        })
                        .flatMap(bookService::save);
            }).subscribe();
        };
    }
}
