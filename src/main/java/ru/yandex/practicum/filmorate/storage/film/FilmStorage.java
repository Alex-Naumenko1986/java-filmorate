package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film getFilmById(int id);

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int filmId);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);


    List<Film> getMostPopularFilms(int count);
}
