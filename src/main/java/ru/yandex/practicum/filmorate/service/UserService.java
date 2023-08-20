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
        User user = userStorage.getUserById(id);
        return replaceUserNameWithLoginIfNameIsEmpty(user);
    }

    public List<User> getAllUsers() {
        List<User> allUsers = userStorage.getAllUsers();
        return allUsers.stream().map(this::replaceUserNameWithLoginIfNameIsEmpty).collect(Collectors.toList());
    }

    public User addUser(User user) {
        User newUser = userStorage.addUser(user);
        return replaceUserNameWithLoginIfNameIsEmpty(newUser);
    }

    public User updateUser(User user) {
        User updatedUser = userStorage.updateUser(user);
        return replaceUserNameWithLoginIfNameIsEmpty(updatedUser);
    }

    public boolean addFriend(int userId, int otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        boolean isAddedToUserFriends = user.addFriend(otherUserId);
        boolean isAddedToOtherUserFriends = otherUser.addFriend(userId);
        if (!isAddedToUserFriends || !isAddedToOtherUserFriends) {
            return false;
        }
        userStorage.updateUser(user);
        userStorage.updateUser(otherUser);
        return true;
    }

    public boolean removeFriend(int userId, int otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        boolean isRemovedFromUserFriends = user.removeFriend(otherUserId);
        boolean isRemovedFromOtherUserFriends = otherUser.removeFriend(userId);
        if (!isRemovedFromUserFriends || !isRemovedFromOtherUserFriends) {
            return false;
        }
        userStorage.updateUser(user);
        userStorage.updateUser(otherUser);
        return true;
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

    private User replaceUserNameWithLoginIfNameIsEmpty(User user) {
        User userClone = User.builder().id(user.getId()).login(user.getLogin()).name(user.getName())
                .birthday(user.getBirthday()).email(user.getEmail()).build();
        if (userClone.getName() == null || userClone.getName().isBlank()) {
            userClone.setName(userClone.getLogin());
        }
        return userClone;
    }
}
