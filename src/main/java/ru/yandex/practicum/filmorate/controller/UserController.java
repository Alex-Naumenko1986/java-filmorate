package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IllegalRequestException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Integer, User> idToUser = new HashMap<>();
    private int generatedId = 0;

    @GetMapping()
    public List<User> getAllUsers() {
        return new ArrayList<>(idToUser.values());
    }

    @PostMapping()
    public User addUser(@Valid @RequestBody User user){
        if (idToUser.containsKey(user.getId())) {
            log.error("У добавляемого пользователя не должно быть id. Пользователь с id: {} уже существует",
                    user.getId());
            throw new IllegalRequestException("У добавляемого пользователя не должно быть id");
        }
        int id = generateId();
        user.setId(id);
        idToUser.put(id, user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping()
    public User updateFilm(@Valid @RequestBody User user) {
        if (!idToUser.containsKey(user.getId())) {
            log.error("Пользователя с id: {} не существует", user.getId());
            throw new IllegalRequestException(String.format("Пользователя с id %d не существует", user.getId()));
        }
        idToUser.replace(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }
    private int generateId() {
        return ++generatedId;
    }
}
