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
    void shouldReturnCorrectRatingById() {
        Rating mpa = ratingStorage.getRatingById(1);
        Rating expectedMpa = new Rating(1, "G");
        assertEquals(expectedMpa, mpa, "Полученный рейтинг не совпадает с ожидаемым");
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
    void shouldReturnAllRatings() {
        List<Rating> ratings = ratingStorage.getAllRatings();
        assertAll("Проверка полученного списка всех рейтингов",
                () -> assertTrue(ratings.size() > 0, "Получен пустой список рейтингов"),
                () -> assertEquals("G", ratings.get(0).getName(), "Неверное название рейтинга"));
    }
}
