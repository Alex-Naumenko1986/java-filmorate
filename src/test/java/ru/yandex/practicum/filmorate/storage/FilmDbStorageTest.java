package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    private Film film1;
    private Film film2;
    private Film film3;
    private User user1;
    private User user2;
    private Rating rating1;
    private Rating rating2;
    private Genre genre1;
    private Genre genre2;
    private Genre genre3;

    @BeforeEach
    void beforeEach() {
        rating1 = new Rating(1);
        rating2 = new Rating(2);
        genre1 = new Genre(1);
        genre2 = new Genre(2);
        genre3 = new Genre(3);
        TreeSet<Genre> filmGenres = new TreeSet<>();
        filmGenres.add(genre1);

        film1 = Film.builder().name("Film 1").description("description 1").duration(100)
                .releaseDate(LocalDate.of(2010, 3, 20)).mpa(rating1).genres(filmGenres).build();
        film2 = Film.builder().name("Film 2").description("description 2").duration(150)
                .releaseDate(LocalDate.of(2015, 5, 15)).mpa(rating2).genres(filmGenres).build();
        film3 = Film.builder().name("Film 3").description("description 3").duration(200)
                .releaseDate(LocalDate.of(2005, 1, 11)).mpa(rating1).genres(filmGenres).build();

        user1 = User.builder().login("login1").name("user1").email("user1@yandex.ru")
                .birthday(LocalDate.of(2000, 3, 20)).build();
        user2 = User.builder().login("login2").name("user2").email("user2@yandex.ru")
                .birthday(LocalDate.of(2010, 4, 5)).build();

        filmStorage.addFilm(film1);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
    }

    @Test
    void shouldAddIdWhenAddFilm() {
        Film film = filmStorage.addFilm(film2);
        assertTrue(film.getId() > 0, "Неверный id добавленного фильма");
    }

    @Test
    void shouldAddFilmSuccessfully() {
        Film film = filmStorage.addFilm(film2);
        assertEquals(film2, film, "Фильмы не совпадают");
    }

    @Test
    void shouldThrowExceptionWhenAddingFilmWithExistingId() {
        film2.setId(1);
        Exception e = assertThrows(FilmAlreadyExistsException.class, () -> {
            filmStorage.addFilm(film2);
        });
        String expectedMessage = "Фильм с id 1 уже существует";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldReturnFilmById() {
        Film film = filmStorage.getFilmById(1);
        assertEquals(film1, film, "Фильмы не совпадают");
    }

    @Test
    void shouldThrowExceptionWhenGetFilmWithNonexistentId() {
        Exception e = assertThrows(FilmNotFoundException.class, () -> {
            filmStorage.getFilmById(100);
        });
        String expectedMessage = "Фильм с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldReturnAllFilms() {
        filmStorage.addFilm(film2);
        List<Film> films = filmStorage.getAllFilms();
        List<Film> expectedFilms = List.of(film1, film2);
        assertAll("Проверка полученного списка всех фильмов",
                () -> assertFalse(films.isEmpty(), "Получен пустой список фильмов"),
                () -> assertEquals(expectedFilms, films, "Списки фильмов не совпадают"));
    }

    @Test
    void shouldReturnEmptyListWhenGetAllFilmsFromEmptyDb() {
        filmStorage.deleteFilm(1);
        List<Film> films = filmStorage.getAllFilms();
        assertTrue(films.isEmpty(), "Полученный список фильмов не пустой");
    }

    @Test
    void shouldUpdateFilmSuccessfully() {
        film1.setDuration(250);
        film1.setName("Updated film1");
        film1.setMpa(rating2);
        TreeSet<Genre> filmGenres = new TreeSet<>();
        filmGenres.add(genre2);
        filmGenres.add(genre3);
        film1.setGenres(filmGenres);

        filmStorage.updateFilm(film1);

        Film updatedFilmFromDb = filmStorage.getFilmById(1);
        assertEquals(film1, updatedFilmFromDb, "Фильмы не совпадают");
    }

    @Test
    void shouldThrowExceptionWhenUpdateFilmWithNonexistentId() {
        film1.setId(10);
        Exception e = assertThrows(FilmNotFoundException.class, () -> {
            filmStorage.updateFilm(film1);
        });
        String expectedMessage = "Фильм с идентификатором 10 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldDeleteFilmSuccessfully() {
        filmStorage.deleteFilm(1);

        Exception e = assertThrows(FilmNotFoundException.class, () -> {
            filmStorage.getFilmById(1);
        });
        String expectedMessage = "Фильм с идентификатором 1 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldThrowExceptionWhenDeleteFilmWithNonexistentId() {
        Exception e = assertThrows(FilmNotFoundException.class, () -> {
            filmStorage.deleteFilm(100);
        });
        String expectedMessage = "Фильм с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldAddLikeSuccessfully() {
        filmStorage.addLike(1, 1);
        Film film1FromDb = filmStorage.getFilmById(1);
        Set<Integer> expectedUserWhoLikedIds = Collections.singleton(1);
        assertEquals(expectedUserWhoLikedIds, film1FromDb.getLikes(), "id пользователей, поставивших лайк," +
                "не совпадает");
    }

    @Test
    void shouldNotAddDuplicateLikeWhenAddLikeWithSameUserId() {
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 1);
        Film film1FromDb = filmStorage.getFilmById(1);
        Set<Integer> expectedUserWhoLikedIds = Collections.singleton(1);
        assertEquals(expectedUserWhoLikedIds, film1FromDb.getLikes(), "id пользователей, поставивших лайк," +
                "не совпадает");
    }

    @Test
    void shouldRemoveLikeSuccessfully() {
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        filmStorage.removeLike(1, 1);

        Film film1FromDb = filmStorage.getFilmById(1);
        Set<Integer> expectedUserWhoLikedIds = Collections.singleton(2);
        assertEquals(expectedUserWhoLikedIds, film1FromDb.getLikes(), "id пользователей, поставивших лайк," +
                "не совпадает");
    }

    @Test
    void shouldReturnCorrectOrderOfPopularFilms() {
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        filmStorage.addLike(2, 1);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(3, 1);
        List<Film> films = filmStorage.getMostPopularFilms(5);
        assertAll("Проверка полученного списка самых популярых фильмов",
                () -> assertEquals(3, films.size(), "Неверный размер полученного списка фильмов"),
                () -> assertEquals("Film 2", films.get(0).getName(),
                        "Неверное название самого популярного фильма"),
                () -> assertEquals("Film 3", films.get(1).getName(),
                        "Неверное название второго по популярности фильма"),
                () -> assertEquals("Film 1", films.get(2).getName(),
                        "Неверное название третьего по популярности фильма"));
    }

    @Test
    void shouldReturnEmptyListOfPopularFilmsWhenNoFilmsAdded() {
        filmStorage.deleteFilm(1);
        List<Film> films = filmStorage.getMostPopularFilms(5);
        assertTrue(films.isEmpty(), "Полученный список фильмов не пустой");
    }

    @Test
    void shouldReturnTwoMostPopularFilmsWhenParamCountIsTwo() {
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        filmStorage.addLike(2, 1);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(3, 1);
        List<Film> films = filmStorage.getMostPopularFilms(2);
        assertAll("Проверка полученного списка самых популярых фильмов",
                () -> assertEquals(2, films.size(), "Неверный размер полученного списка фильмов"),
                () -> assertEquals("Film 2", films.get(0).getName(),
                        "Неверное название самого популярного фильма"),
                () -> assertEquals("Film 3", films.get(1).getName(),
                        "Неверное название второго по популярности фильма"));
    }
}
