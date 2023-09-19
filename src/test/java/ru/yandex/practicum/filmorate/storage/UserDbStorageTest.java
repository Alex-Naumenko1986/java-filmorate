package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final UserDbStorage userStorage;

    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder().login("login1").name("user1").email("user1@yandex.ru")
                .birthday(LocalDate.of(2000, 3, 20)).build();

        user2 = User.builder().login("login2").name("user2").email("user2@yandex.ru")
                .birthday(LocalDate.of(2010, 4, 5)).build();

        user3 = User.builder().login("login3").name("user3").email("user3@yandex.ru")
                .birthday(LocalDate.of(1990, 12, 18)).build();

        user4 = User.builder().login("login4").name("user4").email("user4@yandex.ru")
                .birthday(LocalDate.of(1995, 1, 18)).build();

        userStorage.addUser(user1);
    }

    @Test
    void shouldAddUserSuccessfully() {
        User addedUser = userStorage.addUser(user2);
        assertEquals(user2, addedUser, "Пользователи не совпадают");
    }

    @Test
    void shouldAddIdWhenAddUser() {
        User addedUser = userStorage.addUser(user2);
        assertTrue(addedUser.getId() > 0, "Неверный id добавленного пользователя");
    }

    @Test
    void shouldThrowExceptionWhenAddingUserWithExistingId() {
        user2.setId(1);
        Exception e = assertThrows(UserAlreadyExistsException.class, () -> {
            userStorage.addUser(user2);
        });
        String expectedMessage = "Пользователь с id 1 уже существует";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldReturnUserById() {
        User user = userStorage.getUserById(1);
        assertEquals(user1, user, "Пользователи не совпадают");
    }

    @Test
    void shouldThrowExceptionWhenGetUserWithNonexistentId() {
        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userStorage.getUserById(100);
        });
        String expectedMessage = "Пользователь с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldReturnAllUsers() {
        userStorage.addUser(user2);
        List<User> users = userStorage.getAllUsers();
        List<User> expectedUsers = List.of(user1, user2);
        assertAll("Проверка полученного списка всех пользователей",
                () -> assertFalse(users.isEmpty(), "Получен пустой список пользователей"),
                () -> assertEquals(expectedUsers, users, "Списки пользователей не совпадают"));
    }

    @Test
    void shouldReturnEmptyListWhenGetAllUsersFromEmptyDb() {
        userStorage.deleteUser(1);
        List<User> users = userStorage.getAllUsers();
        assertTrue(users.isEmpty(), "Полученный список пользователей не пустой");
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        userStorage.addUser(user2);
        user1.addFriend(2);
        user1.setName("updatedUser1");
        user1.setEmail("updated1@yandex.ru");
        userStorage.updateUser(user1);

        User updatedUserFromDb = userStorage.getUserById(1);
        assertEquals(user1, updatedUserFromDb, "Пользователи не совпадают");
    }

    @Test
    void shouldThrowExceptionWhenUpdateUserWithNonexistentId() {
        user1.setId(10);
        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userStorage.updateUser(user1);
        });
        String expectedMessage = "Пользователь с идентификатором 10 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        userStorage.deleteUser(1);

        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userStorage.getUserById(1);
        });
        String expectedMessage = "Пользователь с идентификатором 1 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");

    }

    @Test
    void shouldThrowExceptionWhenDeleteUserWithNonexistentId() {
        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userStorage.deleteUser(100);
        });
        String expectedMessage = "Пользователь с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldAddFriendSuccessfully() {
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        User user1FromDb = userStorage.getUserById(1);
        Set<Integer> expectedFriendIds = Collections.singleton(2);
        assertEquals(expectedFriendIds, user1FromDb.getFriendIds(), "id друзей не совпадают");
    }

    @Test
    void shouldThrowExceptionWhenAddFriendWithNonexistentUserId() {
        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userStorage.addFriend(100, 1);
        });
        String expectedMessage = "Пользователь с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldThrowExceptionWhenAddFriendWithNonexistentFriendId() {
        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userStorage.addFriend(1, 100);
        });
        String expectedMessage = "Пользователь с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldNotAddDuplicateFriendWhenAddFriendWithSameId() {
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(1, 2);
        User user1FromDb = userStorage.getUserById(1);
        Set<Integer> expectedFriendIds = Collections.singleton(2);
        assertEquals(expectedFriendIds, user1FromDb.getFriendIds(), "id друзей не совпадают");
    }

    @Test
    void shouldRemoveFriendSuccessfully() {
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        userStorage.removeFriend(1, 2);
        User user1FromDb = userStorage.getUserById(1);
        assertTrue(user1FromDb.getFriendIds().isEmpty(), "Список друзей после удаления не пустой");
    }

    @Test
    void shouldThrowExceptionWhenRemoveFriendWithNonexistentUserId() {
        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userStorage.removeFriend(100, 1);
        });
        String expectedMessage = "Пользователь с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldThrowExceptionWhenRemoveFriendWithNonexistentFriendId() {
        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userStorage.removeFriend(1, 100);
        });
        String expectedMessage = "Пользователь с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldReturnEmptyFriendListWhenNoFriendsAdded() {
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        List<User> friends = userStorage.getUserFriends(2);
        assertTrue(friends.isEmpty(), "Полученный список друзей не пустой");
    }

    @Test
    void shouldReturnUserFriendsSuccessfully() {
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(1, 3);
        List<User> friends = userStorage.getUserFriends(1);
        List<User> expectedFriends = List.of(user2, user3);
        assertEquals(expectedFriends, friends, "Списки друзей не совпадают");
    }

    @Test
    void shouldReturnCommonFriendsSuccessfully() {
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        userStorage.addUser(user4);
        userStorage.addFriend(1, 3);
        userStorage.addFriend(1, 4);
        userStorage.addFriend(2, 1);
        userStorage.addFriend(2, 3);
        userStorage.addFriend(2, 4);
        List<User> commonFriends = userStorage.getCommonFriends(1, 2);
        assertAll("Проверка списка общих друзей",
                () -> assertEquals(2, commonFriends.size(),
                        "Размер списка общих друзей не совпадает с ожидаемым"),
                () -> assertEquals("user3", commonFriends.get(0).getName(),
                        "Имя общего друга не совпадает с ожидаемым"),
                () -> assertEquals("user4", commonFriends.get(1).getName(),
                        "Имя общего друга не совпадает с ожидаемым"));
    }

    @Test
    void shouldReturnEmptyListWhenNoCommonFriends() {
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        userStorage.addUser(user4);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(1, 3);
        userStorage.addFriend(2, 1);
        userStorage.addFriend(2, 4);
        List<User> commonFriends = userStorage.getCommonFriends(1, 2);
        assertTrue(commonFriends.isEmpty(), "Полученный список друзей не пустой");
    }

    @Test
    void shouldThrowExceptionWhenGetCommonFriendsWithNonexistentUserId() {
        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userStorage.removeFriend(100, 1);
        });
        String expectedMessage = "Пользователь с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }

    @Test
    void shouldThrowExceptionWhenGetCommonFriendsWithNonexistentOtherUserId() {
        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userStorage.removeFriend(1, 100);
        });
        String expectedMessage = "Пользователь с идентификатором 100 не найден";
        assertEquals(expectedMessage, e.getMessage(), "Неверное сообщение исключения");
    }
}
