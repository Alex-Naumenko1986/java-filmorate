package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping()
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Map<String, String> addFriend(@PathVariable int id, @PathVariable int friendId) {
        boolean isAdded = userService.addFriend(id, friendId);
        if (isAdded) {
            return Map.of("message", "Пользователи добавлены в друзья");
        } else {
            return Map.of("message", "Пользователи не добавлены в друзья. Возможно, они уже являются друзьями");
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Map<String, String> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        boolean isRemoved = userService.removeFriend(id, friendId);
        if (isRemoved) {
            return Map.of("message", "Пользователи удалены из друзей");
        } else {
            return Map.of("message", "Пользователи не удалены из друзей. Возможно, они не были друзьями");
        }
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
