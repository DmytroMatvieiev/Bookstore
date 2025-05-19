package org.dmdev.bookstore.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "books")
public class Book {
    @Id
    private UUID UUID;
    @Column("isbn")
    private String ISBN;
    @Column("title")
    private String title;
    @Column("pages")
    private int pages;
    @Column("publication_date")
    private LocalDate publicationDate;
    @Column("author")
    private UUID author;
}
