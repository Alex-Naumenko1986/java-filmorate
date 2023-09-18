package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Component("filmDBStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;

    @Override
    public Film getFilmById(int id) {
        String sql = "select * from films where film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден", id));
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getId() != null) {
            String sql = "select * from films where film_id = ?";
            List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), film.getId());
            if (films.size() != 0) {
                throw new FilmAlreadyExistsException(String.format("Фильм с id %d уже существует", film.getId()));
            }
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(filmId);
        if (film.getGenres() == null) {
            film.setGenres(new TreeSet<>());
        }

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        String insertGenresSql = "insert into film_genres(film_id, genre_id) values (?, ?)";
        List<Object[]> batchArgsList = new ArrayList<>();

        for (Genre genre : film.getGenres()) {
            Object[] objectArray = {filmId, genre.getId()};
            batchArgsList.add(objectArray);
        }

        jdbcTemplate.batchUpdate(insertGenresSql, batchArgsList);

        String insertLikesSql = "insert into film_likes(film_id, user_id) values (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();

        for (Integer userWhoLikedId : film.getLikes()) {
            Object[] objectArray = {filmId, userWhoLikedId};
            batchArgsList.add(objectArray);
        }

        jdbcTemplate.batchUpdate(insertLikesSql, batchArgs);

        film.setGenres(getFilmGenresFromDb(filmId));
        film.setMpa(getMpaById(film.getMpa().getId()));

        log.info("В базу данных добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());

        String sql = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "where film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getId());

        if (film.getGenres() == null) {
            film.setGenres(new TreeSet<>());
        }

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        String deleteGenresSql = "delete from film_genres where film_id = ? and genre_id = ?";
        Set<Genre> genresToDelete = new HashSet<>(getFilmGenresFromDb(film.getId()));
        genresToDelete.removeAll(film.getGenres());

        List<Object[]> batchArgs = new ArrayList<>();

        for (Genre genre : genresToDelete) {
            Object[] objectArray = {film.getId(), genre.getId()};
            batchArgs.add(objectArray);
        }

        jdbcTemplate.batchUpdate(deleteGenresSql, batchArgs);

        String insertGenresSql = "insert into film_genres(film_id, genre_id) values (?, ?)";
        Set<Genre> genresToInsert = new HashSet<>(film.getGenres());
        genresToInsert.removeAll(getFilmGenresFromDb(film.getId()));

        batchArgs = new ArrayList<>();

        for (Genre genre : genresToInsert) {
            Object[] objectArray = {film.getId(), genre.getId()};
            batchArgs.add(objectArray);
        }

        jdbcTemplate.batchUpdate(insertGenresSql, batchArgs);

        String deleteLikesSql = "delete from film_likes where film_id = ? and user_id = ?";
        Set<Integer> likesToDelete = new HashSet<>(getLikesInDb(film.getId()));
        likesToDelete.removeAll(film.getLikes());

        batchArgs = new ArrayList<>();

        for (Integer userWhoLikedId : likesToDelete) {
            Object[] objectArray = {film.getId(), userWhoLikedId};
            batchArgs.add(objectArray);
        }

        jdbcTemplate.batchUpdate(deleteLikesSql, batchArgs);

        String insertLikesSql = "insert into film_likes(film_id, user_id) values (?, ?)";
        Set<Integer> likesToInsert = new HashSet<>(film.getLikes());
        likesToInsert.removeAll(getLikesInDb(film.getId()));

        batchArgs = new ArrayList<>();

        for (Integer userWhoLikedId : likesToInsert) {
            Object[] objectArray = {film.getId(), userWhoLikedId};
            batchArgs.add(objectArray);
        }
        jdbcTemplate.batchUpdate(insertLikesSql, batchArgs);

        film.setGenres(getFilmGenresFromDb(film.getId()));
        film.setMpa(getMpaById(film.getMpa().getId()));

        log.info("В базе данных обновлен фильм: {}", film);
        return film;
    }

    @Override
    public void deleteFilm(int filmId) {
        getFilmById(filmId);

        String deleteLikesSql = "delete from film_likes where film_id = ?";
        jdbcTemplate.update(deleteLikesSql, filmId);

        String deleteGenresSql = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(deleteGenresSql, filmId);

        String deleteFilmSql = "delete from films where film_id = ?";
        jdbcTemplate.update(deleteFilmSql, filmId);

        log.info("Из базы данных удален фильм с id: {}", filmId);
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (!getLikesInDb(filmId).contains(userId)) {
            String sql = "insert into film_likes(film_id, user_id) values (?, ?)";
            jdbcTemplate.update(sql, filmId, userId);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (getLikesInDb(filmId).contains(userId)) {
            String sql = "delete from film_likes where film_id = ? and user_id = ?";
            jdbcTemplate.update(sql, filmId, userId);
        }
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        String sql = "select f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "from films as f left join film_likes as fl on f.film_id = fl.film_id " +
                "group by f.film_id order by count(fl.user_id) desc limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Set<Integer> getLikesInDb(int filmId) {
        String sql = "select * from film_likes where film_id = ?";
        List<Integer> userWhoLikedIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"),
                filmId);
        return new HashSet<>(userWhoLikedIds);
    }

    private TreeSet<Genre> getFilmGenresFromDb(int filmId) {
        String sql = "select * from film_genres where film_id = ?";
        List<Genre> filmGenres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
        return new TreeSet<>(filmGenres);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int ratingId = rs.getInt("rating_id");
        Rating rating = getMpaById(ratingId);
        Set<Integer> likeIds = getLikesInDb(id);
        TreeSet<Genre> genres = getFilmGenresFromDb(id);
        return Film.builder().id(id).name(name).description(description).releaseDate(releaseDate)
                .duration(duration).mpa(rating).likes(likeIds).genres(genres).build();
    }

    private Rating getMpaById(int id) {
        return ratingStorage.getRatingById(id);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int genreId = rs.getInt("genre_id");
        return genreStorage.getGenreById(genreId);
    }
}
