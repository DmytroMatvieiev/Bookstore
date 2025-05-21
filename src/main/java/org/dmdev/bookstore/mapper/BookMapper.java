package org.dmdev.bookstore.mapper;

import org.dmdev.bookstore.domain.Book;
import org.dmdev.bookstore.dto.BookDTO;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book dtoToBook(BookDTO dto) {
        return Book.builder()
                .id(dto.id())
                .ISBN(dto.isbn())
                .title(dto.title())
                .pages(dto.pages())
                .publicationDate(dto.publishedDate())
                .authorId(dto.authorId())
                .build();
    }

    public BookDTO bookToDto(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getISBN())
                .publishedDate(book.getPublicationDate())
                .pages(book.getPages())
                .authorId(book.getAuthorId())
                .build();
    }
}
