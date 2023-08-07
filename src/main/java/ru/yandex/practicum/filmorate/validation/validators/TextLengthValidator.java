package ru.yandex.practicum.filmorate.validation.validators;

import ru.yandex.practicum.filmorate.validation.annotations.Length;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TextLengthValidator implements ConstraintValidator<Length, String> {
    private int maxLength;

    @Override
    public void initialize(Length constraintAnnotation) {
        maxLength = constraintAnnotation.length();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.length() <= maxLength;
    }
}
