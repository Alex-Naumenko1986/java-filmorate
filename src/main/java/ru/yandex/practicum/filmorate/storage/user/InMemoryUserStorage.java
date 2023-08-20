package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> idToUser = new HashMap<>();
    private int generatedId = 0;

    @Override
    public User getUserById(int id) {
        if (!idToUser.containsKey(id)) {
            log.error("Пользователь с id {} не найден", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
        ;
        return idToUser.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(idToUser.values());
    }

    @Override
    public User addUser(User user) {
        if (idToUser.containsKey(user.getId())) {
            log.error("Пользователь с id {} уже существует", user.getId());
            throw new UserAlreadyExistsException(String.format("Ошибка при создании пользователя. Пользователь" +
                    "с id %d уже существует", user.getId()));
        }
        int id = generateId();
        user.setId(id);
        idToUser.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!idToUser.containsKey(user.getId())) {
            log.error("Пользователя с id {} не существует", user.getId());
            throw new UserNotFoundException(String.format("Ошибка при обновлении пользователя. Пользователя" +
                    "с id %d не существует", user.getId()));
        }
        idToUser.replace(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        if (!idToUser.containsKey(userId)) {
            log.error("Пользователя с id {} не существует", userId);
            throw new UserNotFoundException(String.format("Ошибка при удалении пользователя. Пользователя" +
                    "с id %d не существует", userId));
        }
        idToUser.remove(userId);
    }

    private int generateId() {
        return ++generatedId;
    }
}
