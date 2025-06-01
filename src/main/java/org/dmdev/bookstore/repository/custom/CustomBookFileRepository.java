package org.dmdev.bookstore.repository.custom;

import org.dmdev.bookstore.domain.BookFile;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomBookFileRepository {
    Mono<Void> saveBookFiles(List<BookFile> bookFiles);
}
