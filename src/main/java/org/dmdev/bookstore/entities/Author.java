package org.dmdev.bookstore.entities;

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
@Table("authors")
public class Author {
    @Id
    private UUID id;
    @Column("firstname")
    private String firstname;
    @Column("lastname")
    private String lastname;
    @Column("birthdate")
    private LocalDate birthdate;
    @Column("death_date ")
    private LocalDate dateOfDeath;
    @Transient
    private List<Book> books;
}
