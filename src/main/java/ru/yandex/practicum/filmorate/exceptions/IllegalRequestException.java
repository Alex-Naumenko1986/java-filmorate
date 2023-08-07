package ru.yandex.practicum.filmorate.exceptions;

public class IllegalRequestException extends RuntimeException{
    public IllegalRequestException(String message) {
        super(message);
    }
}
