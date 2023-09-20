package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingDbStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Rating> getAllRatings() {
        String sql = "select rating_id, name from ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeRating(rs));
    }

    @Override
    public Rating getRatingById(int id) {
        String sql = "select rating_id, name from ratings where rating_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeRating(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new RatingNotFoundException(String.format("Рейтинг с идентификатором %d не найден", id));
        }
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        int id = rs.getInt("rating_id");
        String name = rs.getString("name");

        return new Rating(id, name);
    }
}
