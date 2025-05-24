package org.dmdev.bookstore.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "books")
public class Book {
    @Id
    private UUID id;
    @Column("isbn")
    private String ISBN;
    @Column("title")
    private String title;
    @Column("pages")
    private int pages;
    @Column("publication_date")
    private LocalDate publicationDate;
    @Column("author_id")
    private UUID authorId;
    @Transient
    private List<Genre> genres;
}
