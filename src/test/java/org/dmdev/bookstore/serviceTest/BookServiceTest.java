package org.dmdev.bookstore.serviceTest;

import org.dmdev.bookstore.domain.Author;
import org.dmdev.bookstore.domain.Book;
import org.dmdev.bookstore.domain.BookFile;
import org.dmdev.bookstore.dto.BookDTO;
import org.dmdev.bookstore.dto.BookFileDTO;
import org.dmdev.bookstore.dto.GenreDTO;
import org.dmdev.bookstore.mapper.BookFileMapper;
import org.dmdev.bookstore.mapper.BookMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.AuthorRepository;
import org.dmdev.bookstore.repository.BookFileRepository;
import org.dmdev.bookstore.repository.BookRepository;
import org.dmdev.bookstore.repository.GenreRepository;
import org.dmdev.bookstore.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    AuthorRepository authorRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    GenreRepository genreRepository;
    @Mock
    BookFileRepository bookFileRepository;
    @Mock
    BookMapper mapper;
    @Mock
    BookFileMapper bookFileMapper;

    @InjectMocks
    BookService bookService;

    @Test
    void shouldReturnFailIfBookHasId() {
        BookDTO bookDTO = mock(BookDTO.class);
        when(bookDTO.id()).thenReturn(UUID.randomUUID());

        StepVerifier.create(bookService.save(bookDTO))
                .expectNextMatches(response ->
                        response.getStatus().equals(ResponseModel.FAIL_STATUS) &&
                                response.getMessage().contains("already exists"))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorIfAuthorNotFound() {
        BookDTO bookDTO = buildValidBookDTOWithoutId();
        when(authorRepository.findById(bookDTO.authorId())).thenReturn(Mono.empty());

        StepVerifier.create(bookService.save(bookDTO))
                .expectNextMatches(response ->
                        response.getStatus().equals(ResponseModel.FAIL_STATUS) &&
                                response.getMessage().contains("Failed to save book"))
                .verifyComplete();
    }

    @Test
    void shouldSaveBookSuccessfully() {
        BookDTO bookDTO = buildValidBookDTOWithoutId();
        Author author = new Author(); // stub
        Book book = new Book(); book.setId(UUID.randomUUID()); book.setTitle(bookDTO.title());

        when(authorRepository.findById(bookDTO.authorId())).thenReturn(Mono.just(author));
        when(mapper.dtoToBook(bookDTO)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(Mono.just(book));
        when(genreRepository.saveBookGenres(eq(book.getId()), any())).thenReturn(Mono.empty());
        when(bookFileMapper.toBookFile(any())).thenReturn(new BookFile());
        when(bookFileRepository.saveBookFiles(any())).thenReturn(Mono.empty());

        StepVerifier.create(bookService.save(bookDTO))
                .expectNextMatches(response ->
                        response.getStatus().equals(ResponseModel.SUCCESS_STATUS) &&
                                response.getMessage().contains("created"))
                .verifyComplete();
    }

    @Test
    void shouldHandleRepositoryFailureGracefully() {
        BookDTO bookDTO = buildValidBookDTOWithoutId();
        Author author = new Author();
        Book book = new Book(); book.setId(UUID.randomUUID());

        when(authorRepository.findById(bookDTO.authorId())).thenReturn(Mono.just(author));
        when(mapper.dtoToBook(bookDTO)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(bookService.save(bookDTO))
                .expectNextMatches(response ->
                        response.getStatus().equals(ResponseModel.FAIL_STATUS) &&
                                response.getMessage().contains("Failed to save"))
                .verifyComplete();
    }

    @Test
    void shouldReturnBooksSuccessfully() {
        int limit = 1, size = 2, offset = 0;
        Book book1 = new Book(); book1.setTitle("Book 1");
        Book book2 = new Book(); book2.setTitle("Book 2");

        BookDTO dto1 = buildValidBookDTOWithoutId();
        BookDTO dto2 = buildValidBookDTOWithoutId();

        when(bookRepository.findAll(size, offset)).thenReturn(Flux.just(book1, book2));
        when(mapper.bookToDto(book1)).thenReturn(dto1);
        when(mapper.bookToDto(book2)).thenReturn(dto2);

        StepVerifier.create(bookService.findAll(limit, size))
                .expectNextMatches(response ->
                        response.getStatus().equals(ResponseModel.SUCCESS_STATUS) &&
                                response.getMessage().contains("retrieved") &&
                                ((List<?>) response.getData()).size() == 2)
                .verifyComplete();
    }

    @Test
    void shouldHandleErrorGracefully() {
        int limit = 1, size = 2, offset = 0;

        when(bookRepository.findAll(size, offset)).thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(bookService.findAll(limit, size))
                .expectNextMatches(response ->
                        response.getStatus().equals(ResponseModel.FAIL_STATUS) &&
                                response.getMessage().contains("Error retrieving books"))
                .verifyComplete();
    }

    @Test
    void shouldReturnFailIfBookNotFound() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(bookService.findById(id))
                .expectNextMatches(response ->
                        response.getStatus().equals(ResponseModel.FAIL_STATUS) &&
                                response.getMessage().contains("not found"))
                .verifyComplete();
    }

    @Test
    void shouldHandleRepositoryErrorGracefully() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Mono.error(new RuntimeException("DB down")));

        StepVerifier.create(bookService.findById(id))
                .expectNextMatches(response ->
                        response.getStatus().equals(ResponseModel.FAIL_STATUS) &&
                                response.getMessage().contains("Error:"))
                .verifyComplete();
    }

    private BookDTO buildValidBookDTOWithoutId() {
        return new BookDTO(
                null,
                "Title",
                "isbn",
                LocalDate.now(),
                545,
                UUID.randomUUID(),
                List.of(new GenreDTO(UUID.randomUUID(), "genre")),
                List.of(new BookFileDTO(null, null, "fb2", "file.txt"))
        );
    }
}
