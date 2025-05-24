package org.dmdev.bookstore.repository;

import org.dmdev.bookstore.domain.Book;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends ReactiveCrudRepository<Book, UUID> {

    @Query("SELECT * FROM books ORDER BY publication_date ASC LIMIT :limit OFFSET :offset")
    Flux<Book> findAll(int limit, int offset);

    Flux<Book> findAllByAuthorId(UUID author);

    @Query("""
                SELECT b.* FROM books b
                JOIN book_genres bg ON b.id = bg.book_id
                WHERE bg.genre_id IN (:genreIds)
                GROUP BY b.id
                HAVING COUNT(DISTINCT bg.genre_id) = :count
            """)
    Flux<Book> findBooksByGenres(List<UUID> genreIds, int count);
}
