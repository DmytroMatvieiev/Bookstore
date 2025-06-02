package org.dmdev.bookstore.serviceTest;

import org.dmdev.bookstore.domain.Author;
import org.dmdev.bookstore.dto.AuthorDTO;
import org.dmdev.bookstore.mapper.AuthorMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.AuthorRepository;
import org.dmdev.bookstore.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthorServiceTest {

    private AuthorService authorService;
    private AuthorMapper authorMapper;
    private AuthorRepository authorRepository;

    private static UUID ID_1 = UUID.randomUUID();
    private static UUID ID_2 = UUID.randomUUID();

    static final Author AUTHOR_1 = Author.builder()
            .id(ID_1)
            .firstname("Leo")
            .lastname("Tolstoy")
            .birthdate(LocalDate.of(1828, 9, 9))
            .dateOfDeath(LocalDate.of(1910, 11, 20))
            .build();

    static final Author AUTHOR_2 = Author.builder()
            .id(ID_2)
            .firstname("Fyodor")
            .lastname("Dostoevsky")
            .birthdate(LocalDate.of(1821, 11, 11))
            .dateOfDeath(LocalDate.of(1881, 2, 9))
            .build();

    static final AuthorDTO AUTHOR_DTO_1 = new AuthorDTO(
            ID_1, "Leo", "Tolstoy",
            LocalDate.of(1828, 9, 9),
            LocalDate.of(1910, 11, 20)
    );

    static final AuthorDTO AUTHOR_DTO_2 = new AuthorDTO(
            ID_2, "Fyodor", "Dostoevsky",
            LocalDate.of(1821, 11, 11),
            LocalDate.of(1881, 2, 9)
    );

    private static final AuthorDTO VALID_AUTHOR_DTO = new AuthorDTO(
            null, "Leo", "Tolstoy",
            LocalDate.of(1828, 9, 9),
            LocalDate.of(1910, 11, 20)
    );

    private static final Author MAPPED_AUTHOR = Author.builder()
            .firstname("Leo")
            .lastname("Tolstoy")
            .birthdate(LocalDate.of(1828, 9, 9))
            .dateOfDeath(LocalDate.of(1910, 11, 20))
            .build();

    @BeforeEach
    void setUp() {
        authorRepository = mock(AuthorRepository.class);
        authorMapper = new AuthorMapper();
        authorService = new AuthorService(authorRepository, authorMapper);
    }

    @Test
    void save_ShouldReturnFail_WhenAuthorIdIsPresent() {
        authorService.save(AUTHOR_DTO_1)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Author already exists", response.getMessage());
                })
                .verifyComplete();

        verifyNoInteractions(authorRepository);
    }

    @Test
    void save_ShouldReturnSuccess_WhenAuthorIsNew() {
        AuthorDTO newDto = AUTHOR_DTO_2.toBuilder().id(null).build();
        Author mappedAuthor = AUTHOR_2.toBuilder().id(null).build();
        Author savedAuthor = AUTHOR_2;

        //when(authorMapper.dtoToAuthor(newDto)).thenReturn(mappedAuthor);
        when(authorRepository.save(mappedAuthor)).thenReturn(Mono.just(savedAuthor));

        authorService.save(newDto)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.SUCCESS_STATUS, response.getStatus());
                    assertTrue(response.getMessage().contains("saved successfully"));
                    assertEquals(savedAuthor, response.getData());
                })
                .verifyComplete();
    }

    @Test
    void findAll_ShouldReturnListOfAuthors() {
        List<Author> authors = List.of(AUTHOR_1, AUTHOR_2);
        List<AuthorDTO> authorsDto = List.of(AUTHOR_DTO_1, AUTHOR_DTO_2);
        when(authorRepository.findAll()).thenReturn(Flux.fromIterable(authors));

        authorService.findAll()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.SUCCESS_STATUS, response.getStatus());
                    assertEquals("Authors retrieved successfully", response.getMessage());
                    assertEquals(authorsDto, response.getData());
                })
                .verifyComplete();
    }

    @Test
    void find_ShouldReturnAuthor_WhenAuthorExists() {
        when(authorRepository.findById(ID_1)).thenReturn(Mono.just(AUTHOR_1));

        StepVerifier.create(authorService.find(ID_1))
                .assertNext(response -> {
                    assertEquals(ResponseModel.SUCCESS_STATUS, response.getStatus());
                    assertEquals("Author found", response.getMessage());

                    AuthorDTO dto = (AuthorDTO) response.getData();
                    assertNotNull(dto);
                    assertEquals("Leo", dto.firstName());
                    assertEquals("Tolstoy", dto.lastName());
                    assertEquals(ID_1, dto.id());
                })
                .verifyComplete();

        verify(authorRepository).findById(ID_1);
    }

    @Test
    void find_ShouldReturnFail_WhenAuthorNotFound() {
        when(authorRepository.findById(ID_1)).thenReturn(Mono.empty());

        StepVerifier.create(authorService.find(ID_1))
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Author not found", response.getMessage());
                    assertNull(response.getData());
                })
                .verifyComplete();

        verify(authorRepository).findById(ID_1);
    }

    @Test
    void delete_ShouldReturnSuccess_WhenAuthorExists() {
        when(authorRepository.findById(ID_1)).thenReturn(Mono.just(AUTHOR_1));
        when(authorRepository.delete(AUTHOR_1)).thenReturn(Mono.empty());

        StepVerifier.create(authorService.delete(ID_1))
                .assertNext(response -> {
                    assertEquals(ResponseModel.SUCCESS_STATUS, response.getStatus());
                    assertEquals("Author deleted successfully", response.getMessage());
                })
                .verifyComplete();

        verify(authorRepository).findById(ID_1);
        verify(authorRepository).delete(AUTHOR_1);
    }

    @Test
    void delete_ShouldReturnFail_WhenErrorOccurs() {
        when(authorRepository.findById(ID_1)).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(authorService.delete(ID_1))
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertTrue(response.getMessage().contains("Error: DB error"));
                })
                .verifyComplete();

        verify(authorRepository).findById(ID_1);
        verify(authorRepository, never()).delete(any());
    }

    @Test
    void update_ShouldReturnFail_WhenIdIsNull() {
        AuthorDTO dtoWithNullId = new AuthorDTO(null, "Leo", "Tolstoy", null, null);

        StepVerifier.create(authorService.update(dtoWithNullId))
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Author ID must not be null", response.getMessage());
                })
                .verifyComplete();

        verifyNoInteractions(authorRepository);
    }
}
