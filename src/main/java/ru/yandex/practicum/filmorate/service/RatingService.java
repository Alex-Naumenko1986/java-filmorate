package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public List<Rating> getAllRatings() {
        return ratingStorage.getAllRatings();
    }

    public Rating getRatingById(int id) {
        return ratingStorage.getRatingById(id);
    }
}
