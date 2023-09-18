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
    void testGetGenreById() {
        Genre genre = genreStorage.getGenreById(1);
        assertEquals(1, genre.getId(), "Идентификатор полученного из БД жанра не равен 1");
        assertEquals("Комедия", genre.getName(), "Название полученного из БД жанра не " +
                "совпадает с ожидаемым");
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
    void testGetAllGenres() {
        List<Genre> genres = genreStorage.getAllgenres();
        assertTrue(genres.size() > 0, "Получен пустой список жанров");
        assertEquals("Комедия", genres.get(0).getName(), "Неверное название жанра");
    }
}
