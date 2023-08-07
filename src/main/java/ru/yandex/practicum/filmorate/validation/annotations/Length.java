package ru.yandex.practicum.filmorate.validation.annotations;

import ru.yandex.practicum.filmorate.validation.validators.TextLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TextLengthValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Length {
    int length() default 200;

    String message() default "Максимальная длина описания - 200 символов";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
