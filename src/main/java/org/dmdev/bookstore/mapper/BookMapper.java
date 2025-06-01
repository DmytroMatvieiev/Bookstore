package org.dmdev.bookstore.mapper;

import lombok.RequiredArgsConstructor;
import org.dmdev.bookstore.domain.Book;
import org.dmdev.bookstore.domain.BookFile;
import org.dmdev.bookstore.domain.Genre;
import org.dmdev.bookstore.dto.BookDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final GenreMapper genreMapper;
    private final BookFileMapper bookFileMapper;

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

    public BookDTO bookDtoToSend(Book book, List<Genre> genres, List<BookFile> bookFiles) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getISBN())
                .publishedDate(book.getPublicationDate())
                .pages(book.getPages())
                .authorId(book.getAuthorId())
                .genres(genres.stream()
                        .map(genreMapper::genreToDto)
                        .toList())
                .bookFiles(bookFiles.stream()
                        .map(bookFileMapper::toBookFileDTO)
                        .toList())
                .build();
    }
}
