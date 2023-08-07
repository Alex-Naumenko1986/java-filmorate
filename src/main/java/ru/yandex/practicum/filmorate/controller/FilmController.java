package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IllegalRequestException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Integer, Film> idToFilm = new HashMap<>();
    private int generatedId = 0;

    @GetMapping()
    public List<Film> getAllFilms() {
        return new ArrayList<>(idToFilm.values());
    }

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        if (idToFilm.containsKey(film.getId())) {
            log.error("У добавляемого фильма не должно быть id. Фильм с id: {} уже существует", film.getId());
            throw new IllegalRequestException("У добавляемого фильма не должно быть id");
        }
        int id = generateId();
        film.setId(id);
        idToFilm.put(id, film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!idToFilm.containsKey(film.getId())) {
            log.error("Фильма с id: {} не существует", film.getId());
            throw new IllegalRequestException(String.format("Фильма с id %d не существует", film.getId()));
        }
        idToFilm.replace(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    private int generateId() {
        return ++generatedId;
    }
}
