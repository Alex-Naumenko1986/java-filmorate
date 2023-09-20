package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Test
    void shouldGetCorrectGenreById() {
        Genre genre = genreStorage.getGenreById(1);
        Genre expectedGenre = new Genre(1, "Комедия");
        assertEquals(expectedGenre, genre, "Полученный из БД жанр не совпадает с ожидаемым");
    }

    @Test
    void shouldThrowExceptionWhenGetGenreWithNonexistentId() {
        Exception e = assertThrows(GenreNotFoundException.class, () -> {
            genreStorage.getGenreById(100);
        });
        String expectedMessage = "Жанр с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldReturnAllGenres() {
        List<Genre> genres = genreStorage.getAllgenres();
        assertAll("Проверка полученного списка всех жанров",
                () -> assertTrue(genres.size() > 0, "Получен пустой список жанров"),
                () -> assertEquals("Комедия", genres.get(0).getName(), "Неверное название жанра"));
    }
}
