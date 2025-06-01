package org.dmdev.bookstore.repository;

import org.dmdev.bookstore.domain.BookFile;
import org.dmdev.bookstore.repository.custom.CustomBookFileRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface BookFileRepository extends ReactiveCrudRepository<BookFile, UUID>, CustomBookFileRepository {

    Flux<BookFile> findByBookId(UUID bookId);

    Flux<BookFile> findBookFilesByBookIdAndFormat(UUID bookId, String format);
}
