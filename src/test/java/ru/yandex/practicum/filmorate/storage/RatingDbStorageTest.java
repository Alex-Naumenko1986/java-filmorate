package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingDbStorageTest {
    private final RatingDbStorage ratingStorage;

    @Test
    void testGetRatingById() {
        Rating mpa = ratingStorage.getRatingById(1);
        assertEquals(1, mpa.getId(), "Идентификатор рейтинга не совпадает с ожидаемым");
        assertEquals("G", mpa.getName(), "Полученное имя рейтинга не совпадает с ожидаемым");
    }

    @Test
    void shouldThrowExceptionWhenGetRatingWithNonexistentId() {
        Exception e = assertThrows(RatingNotFoundException.class, () -> {
            ratingStorage.getRatingById(100);
        });
        String expectedMessage = "Рейтинг с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void testGetAllRatings() {
        List<Rating> ratings = ratingStorage.getAllRatings();
        assertTrue(ratings.size() > 0, "Получен пустой список рейтингов");
        assertEquals("G", ratings.get(0).getName(), "Неверное название рейтинга");
    }
}
