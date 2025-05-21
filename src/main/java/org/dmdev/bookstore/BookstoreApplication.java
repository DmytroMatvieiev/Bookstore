package org.dmdev.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

/*    @Bean
    public CommandLineRunner demo(BookService bookService, AuthorService authorService) {
        return args -> {
*//*            Flux.range(0, 5)
                    .map(i -> Author.builder()
                            .firstname("firstname" + i)
                            .lastname("lastname" + i)
                            .birthdate(LocalDate.of(2000,i+1,1))
                            .build())
                    .flatMap(authorService::save)
                    .subscribe();*//*
            List<UUID> authorIds = List.of(UUID.fromString("7216ec30-9c25-4f98-8335-cfcf258e27d1"),
                    UUID.fromString("36a59290-9ee9-4cec-a595-e4836753cdad"),
                    UUID.fromString("2530cc11-4bd5-4401-ad24-fceee55b6160"),
                    UUID.fromString("5454904e-6aad-4a75-9129-daa45aa8720f"),
                    UUID.fromString("5dbb5071-5c4e-4fe1-a108-813301533cdc"));

            Flux.range(0, 50)
                    .map(i -> {
                        UUID randomAuthor = authorIds.get(i % authorIds.size()); // Cycle through authors
                        return Book.builder()
                                .title("book " + i)
                                .ISBN("ISBN " + i)
                                .author(randomAuthor)
                                .pages(i+130)
                                .publicationDate(LocalDate.of(2000 + (i%12)%4,(i%12)%6+1,i%27+1))
                                .build();
                    })
                    .flatMap(bookService::save)
                    .subscribe();
        };
    }*/
}

