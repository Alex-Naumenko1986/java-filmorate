package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.annotations.Length;
import ru.yandex.practicum.filmorate.validation.annotations.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;



@Data
@Builder
public class Film {
    private int id;
    @NotBlank(message = "Название фильма не должно быть пустым")
    private String name;
    @Length
    private String description;
    @ReleaseDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
}
