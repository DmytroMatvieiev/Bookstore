package org.dmdev.bookstore.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "book_genres")
public class BookGenre {
    @Column("book_id")
    private UUID bookId;
    @Column("genre_id")
    private UUID genreId;
}
