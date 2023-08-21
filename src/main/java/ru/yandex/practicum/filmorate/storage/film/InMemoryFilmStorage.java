package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> idToFilm = new HashMap<>();
    private int generatedId = 0;

    @Override
    public Film getFilmById(int id) {
        if (!idToFilm.containsKey(id)) {
            log.error("Фильм с id {} не найден", id);
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));
        }
        return idToFilm.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(idToFilm.values());
    }

    @Override
    public Film addFilm(Film film) {
        if (idToFilm.containsKey(film.getId())) {
            log.error("Фильм с id {} уже существует", film.getId());
            throw new FilmAlreadyExistsException(String.format("Ошибка при создании фильма. Фильм с id %d " +
                    "уже существует", film.getId()));
        }
        int id = generateId();
        film.setId(id);
        idToFilm.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!idToFilm.containsKey(film.getId())) {
            log.error("Фильма с id {} не существует", film.getId());
            throw new FilmNotFoundException(String.format("Ошибка при обновлении фильма. Фильма с id %d" +
                    "не существует", film.getId()));
        }
        idToFilm.replace(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(int filmId) {
        if (!idToFilm.containsKey(filmId)) {
            log.error("Фильма с id {} не существует", filmId);
            throw new FilmNotFoundException(String.format("Ошибка при удалении фильма. Фильма с id %d" +
                    "не существует", filmId));
        }
        idToFilm.remove(filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return idToFilm.values().stream().sorted((film1, film2) -> Integer.compare(film2.getLikes().size(),
                film1.getLikes().size())).limit(count).collect(Collectors.toList());
    }

    private int generateId() {
        return ++generatedId;
    }
}
