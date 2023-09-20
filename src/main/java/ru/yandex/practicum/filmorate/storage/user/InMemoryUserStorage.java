package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> idToUser = new HashMap<>();
    private int generatedId = 0;

    @Override
    public User getUserById(int id) {
        if (!idToUser.containsKey(id)) {
            log.error("User with id {} was not found", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
        return idToUser.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(idToUser.values());
    }

    @Override
    public User addUser(User user) {
        if (idToUser.containsKey(user.getId())) {
            log.error("User with id {} already exists", user.getId());
            throw new UserAlreadyExistsException(String.format("Ошибка при создании пользователя. Пользователь" +
                    "с id %d уже существует", user.getId()));
        }
        int id = generateId();
        user.setId(id);
        replaceNameWithLoginIfNameIsEmpty(user);
        idToUser.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!idToUser.containsKey(user.getId())) {
            log.error("User with id {} does not exist", user.getId());
            throw new UserNotFoundException(String.format("Ошибка при обновлении пользователя. Пользователя" +
                    "с id %d не существует", user.getId()));
        }
        replaceNameWithLoginIfNameIsEmpty(user);
        idToUser.replace(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        if (!idToUser.containsKey(userId)) {
            log.error("User with id {} does not exist", userId);
            throw new UserNotFoundException(String.format("Ошибка при удалении пользователя. Пользователя" +
                    "с id %d не существует", userId));
        }
        idToUser.remove(userId);
    }

    @Override
    public void addFriend(int userId, int otherUserId) {
        User user = getUserById(userId);
        getUserById(otherUserId);
        user.addFriend(otherUserId);
        updateUser(user);
    }

    @Override
    public void removeFriend(int userId, int otherUserId) {
        User user = getUserById(userId);
        getUserById(otherUserId);
        user.removeFriend(otherUserId);
        updateUser(user);
    }

    @Override
    public List<User> getUserFriends(int userId) {
        User user = getUserById(userId);
        return user.getFriendIds().stream().map(this::getUserById).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);
        Set<Integer> commonFriendsIds = new HashSet<>(user.getFriendIds());
        commonFriendsIds.retainAll(otherUser.getFriendIds());
        return commonFriendsIds.stream().map(this::getUserById).collect(Collectors.toList());
    }

    private void replaceNameWithLoginIfNameIsEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private int generateId() {
        return ++generatedId;
    }
}
