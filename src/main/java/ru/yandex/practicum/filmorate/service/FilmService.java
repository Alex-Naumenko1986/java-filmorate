package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public boolean addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        boolean isLikeAdded = film.addLike(user.getId());
        if (!isLikeAdded) {
            return false;
        }
        filmStorage.updateFilm(film);
        return true;
    }

    public boolean removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        boolean isLikeRemoved = film.removeLike(user.getId());
        if (!isLikeRemoved) {
            return false;
        }
        filmStorage.updateFilm(film);
        return true;
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> allFilms = filmStorage.getAllFilms();
        return allFilms.stream().sorted((film1, film2) -> Integer.compare(film2.getLikes().size(),
                film1.getLikes().size())).limit(count).collect(Collectors.toList());
    }
}
