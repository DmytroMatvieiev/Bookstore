package org.dmdev.bookstore.serviceTest;

import org.dmdev.bookstore.domain.Genre;
import org.dmdev.bookstore.dto.GenreDTO;
import org.dmdev.bookstore.mapper.GenreMapper;
import org.dmdev.bookstore.model.ResponseModel;
import org.dmdev.bookstore.repository.GenreRepository;
import org.dmdev.bookstore.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class GenreServiceTest {

    private GenreService genreService;
    private GenreMapper genreMapper;
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp(){
        genreRepository = mock(GenreRepository.class);
        genreMapper = mock(GenreMapper.class);
        genreService = new GenreService(genreRepository, genreMapper);
    }

    @Test
    void save_ShouldReturnFail_WhenIdIsNotNull() {
        GenreDTO dto = new GenreDTO(UUID.randomUUID(), "Action");

        genreService.save(dto)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Genre already exists", response.getMessage());
                })
                .verifyComplete();

        verifyNoInteractions(genreRepository);
    }

    @Test
    void save_ShouldReturnSuccess_WhenSavedSuccessfully() {
        GenreDTO dto = new GenreDTO(null, "Action");
        Genre genre = new Genre(UUID.randomUUID(), "Action");

        when(genreMapper.dtoToGenre(dto)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(Mono.just(genre));

        genreService.save(dto)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.SUCCESS_STATUS, response.getStatus());
                    assertEquals("Genre saved successfully", response.getMessage());
                })
                .verifyComplete();

        verify(genreRepository).save(genre);
    }

    @Test
    void save_ShouldReturnFail_WhenRepositoryThrows() {
        GenreDTO dto = new GenreDTO(null, "Action");
        Genre genre = new Genre(null, "Action");

        when(genreMapper.dtoToGenre(dto)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(Mono.error(new RuntimeException("DB error")));

        genreService.save(dto)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Error saving genre", response.getMessage());
                })
                .verifyComplete();
    }

    @Test
    void findAllGenres_ShouldReturnResponseModelWithGenres() {
        List<Genre> genres = List.of(
                new Genre(UUID.randomUUID(), "Action"),
                new Genre(UUID.randomUUID(), "Drama")
        );

        List<GenreDTO> genreDTOs = genres.stream()
                .map(g -> new GenreDTO(g.getId(), g.getName()))
                .toList();

        when(genreRepository.findAll()).thenReturn(Flux.fromIterable(genres));
        for (int i = 0; i < genres.size(); i++) {
            when(genreMapper.genreToDto(genres.get(i))).thenReturn(genreDTOs.get(i));
        }

        genreService.findAllGenres()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.SUCCESS_STATUS, response.getStatus());
                    assertEquals("Genres retrieved successfully", response.getMessage());

                    assertTrue(response.getData() instanceof List<?>);
                    List<?> data = (List<?>) response.getData();
                    assertEquals(2, data.size());

                    assertEquals(genreDTOs.get(0), data.get(0));
                    assertEquals(genreDTOs.get(1), data.get(1));
                })
                .verifyComplete();
    }

    @Test
    void findAllGenres_ShouldReturnFailResponse_WhenRepositoryFails() {
        when(genreRepository.findAll()).thenReturn(Flux.error(new RuntimeException("DB error")));

        genreService.findAllGenres()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Error while fetching genres", response.getMessage());
                })
                .verifyComplete();
    }

    @Test
    void delete_ShouldReturnSuccess_WhenGenreExists() {
        UUID genreId = UUID.randomUUID();
        Genre genre = new Genre(genreId, "Fantasy");

        when(genreRepository.findById(genreId)).thenReturn(Mono.just(genre));
        when(genreRepository.delete(genre)).thenReturn(Mono.empty());

        genreService.delete(genreId)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.SUCCESS_STATUS, response.getStatus());
                    assertEquals("Genre deleted successfully", response.getMessage());
                })
                .verifyComplete();

        verify(genreRepository).findById(genreId);
        verify(genreRepository).delete(genre);
    }

    @Test
    void delete_ShouldReturnFail_WhenGenreNotFound() {
        UUID genreId = UUID.randomUUID();

        when(genreRepository.findById(genreId)).thenReturn(Mono.empty());

        genreService.delete(genreId)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Genre not found", response.getMessage());
                })
                .verifyComplete();

        verify(genreRepository).findById(genreId);
        verify(genreRepository, never()).delete(any());
    }

    @Test
    void delete_ShouldReturnFail_WhenDeleteThrows() {
        UUID genreId = UUID.randomUUID();
        Genre genre = new Genre(genreId, "Horror");

        when(genreRepository.findById(genreId)).thenReturn(Mono.just(genre));
        when(genreRepository.delete(genre)).thenReturn(Mono.error(new RuntimeException("DB error")));

        genreService.delete(genreId)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Error deleting genre", response.getMessage());
                })
                .verifyComplete();

        verify(genreRepository).findById(genreId);
        verify(genreRepository).delete(genre);
    }

    @Test
    void update_ShouldReturnSuccess_WhenGenreExists() {
        UUID id = UUID.randomUUID();
        GenreDTO dto = new GenreDTO(id, "Updated Name");
        Genre existing = new Genre(id, "Old Name");
        Genre updated = new Genre(id, "Updated Name");

        when(genreRepository.findById(id)).thenReturn(Mono.just(existing));
        when(genreMapper.dtoToGenre(dto)).thenReturn(updated);
        when(genreRepository.save(updated)).thenReturn(Mono.just(updated));

        genreService.update(dto)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.SUCCESS_STATUS, response.getStatus());
                    assertEquals("Genre updated successfully", response.getMessage());
                })
                .verifyComplete();

        verify(genreRepository).findById(id);
        verify(genreMapper).dtoToGenre(dto);
        verify(genreRepository).save(updated);
    }

    @Test
    void update_ShouldReturnFail_WhenGenreNotFound() {
        UUID id = UUID.randomUUID();
        GenreDTO dto = new GenreDTO(id, "Nonexistent Genre");

        when(genreRepository.findById(id)).thenReturn(Mono.empty());

        genreService.update(dto)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Genre not found", response.getMessage());
                })
                .verifyComplete();

        verify(genreRepository).findById(id);
        verify(genreMapper, never()).dtoToGenre(any());
        verify(genreRepository, never()).save(any());
    }

    @Test
    void update_ShouldReturnFail_WhenIdIsNull() {
        GenreDTO dto = new GenreDTO(null, "No ID");

        genreService.update(dto)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Genre ID must not be null", response.getMessage());
                })
                .verifyComplete();

        verifyNoInteractions(genreRepository);
        verifyNoInteractions(genreMapper);
    }

    @Test
    void update_ShouldReturnFail_WhenSaveThrowsException() {
        UUID id = UUID.randomUUID();
        GenreDTO dto = new GenreDTO(id, "Error Genre");
        Genre existing = new Genre(id, "Old Name");
        Genre toSave = new Genre(id, "Error Genre");

        when(genreRepository.findById(id)).thenReturn(Mono.just(existing));
        when(genreMapper.dtoToGenre(dto)).thenReturn(toSave);
        when(genreRepository.save(toSave)).thenReturn(Mono.error(new RuntimeException("DB error")));

        genreService.update(dto)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(ResponseModel.FAIL_STATUS, response.getStatus());
                    assertEquals("Error: DB error", response.getMessage());
                })
                .verifyComplete();

        verify(genreRepository).findById(id);
        verify(genreMapper).dtoToGenre(dto);
        verify(genreRepository).save(toSave);
    }
}
