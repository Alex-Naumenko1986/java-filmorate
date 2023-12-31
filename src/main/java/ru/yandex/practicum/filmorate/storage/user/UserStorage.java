package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
    User getUserById(int id);

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(int userId);

    void addFriend(int userId, int otherUserId);

    void removeFriend(int userId, int otherUserId);

    List<User> getUserFriends(int userId);

    List<User> getCommonFriends(int userId, int otherUserId);

}
