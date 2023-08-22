package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    final UserStorage userStorage;

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(int userId, int otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        user.addFriend(otherUserId);
        otherUser.addFriend(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(otherUser);
    }

    public void removeFriend(int userId, int otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        user.removeFriend(otherUserId);
        otherUser.removeFriend(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(otherUser);
    }

    public List<User> getUserFriends(int userId) {
        User user = userStorage.getUserById(userId);
        return user.getFriendIds().stream().map(this::getUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        Set<Integer> commonFriendsIds = new HashSet<>(user.getFriendIds());
        commonFriendsIds.retainAll(otherUser.getFriendIds());
        return commonFriendsIds.stream().map(this::getUserById).collect(Collectors.toList());
    }

}
