package org.dmdev.bookstore.repository.custom.impl;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.BookFile;
import org.dmdev.bookstore.repository.custom.CustomBookFileRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomBookFileRepositoryImpl implements CustomBookFileRepository {

    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Void> saveBookFiles(List<BookFile> bookFiles) {
        return Flux.fromIterable(bookFiles)
                .flatMap(bookFile -> template.insert(BookFile.class)
                        .using(bookFile)
                        .then()
                )
                .then();
    }
}
