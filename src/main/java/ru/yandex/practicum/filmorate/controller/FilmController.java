package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping()
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Map<String, String> addLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        boolean isAdded = filmService.addLike(filmId, userId);
        if (isAdded) {
            return Map.of("message", "Лайк добавлен");
        } else {
            return Map.of("message", "Лайк не добавлен. Возможно, пользователь уже ставил " +
                    "лайк данному фильму ранее");
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Map<String, String> removeLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        boolean isRemoved = filmService.removeLike(filmId, userId);
        if (isRemoved) {
            return Map.of("message", "Лайк удален");
        } else {
            return Map.of("message", "Лайк не удален. Возможно, пользователь не ставил лайк данному фильму");
        }
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(value = "count", defaultValue = "10") int count) {
        return filmService.getMostPopularFilms(count);
    }
}
