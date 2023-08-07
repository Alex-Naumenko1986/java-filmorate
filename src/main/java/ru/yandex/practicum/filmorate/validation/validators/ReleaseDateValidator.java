package ru.yandex.practicum.filmorate.validation.validators;

import ru.yandex.practicum.filmorate.validation.annotations.ReleaseDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    private LocalDate releaseDate;
    @Override
    public void initialize(ReleaseDate constraintAnnotation) {
        releaseDate = LocalDate.parse(constraintAnnotation.date());
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        return !date.isBefore(releaseDate);
    }
}
