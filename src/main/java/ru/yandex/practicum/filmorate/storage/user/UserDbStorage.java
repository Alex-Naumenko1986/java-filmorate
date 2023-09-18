package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("UserDbStorage")
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User getUserById(int id) {
        String sql = "select * from users where user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден", id));
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User addUser(User user) {
        if (user.getId() != null) {
            String sql = "select * from users where user_id = ?";
            List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), user.getId());
            if (users.size() != 0) {
                throw new UserAlreadyExistsException(String.format("Пользователь с id %d уже существует",
                        user.getId()));
            }
        }

        replaceNameWithLoginIfNameIsEmpty(user);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        int userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(userId);

        if (user.getFriendIds() == null) {
            user.setFriendIds(new HashSet<>());
            return user;
        }

        String insertFriendIdsSql = "insert into friends (user_id, friend_id) VALUES (?, ?)";

        List<Object[]> batchArgsList = new ArrayList<>();

        for (Integer friendId : user.getFriendIds()) {
            Object[] objectArray = {userId, friendId};
            batchArgsList.add(objectArray);
        }

        jdbcTemplate.batchUpdate(insertFriendIdsSql, batchArgsList);

        log.info("В базу данных добавлен пользователь: {}", user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        replaceNameWithLoginIfNameIsEmpty(user);

        String sql = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ?" +
                "where user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()),
                user.getId());

        if (user.getFriendIds() == null) {
            user.setFriendIds(new HashSet<>());
        }

        Set<Integer> friendsToDeleteFromDb = new HashSet<>(getFriendIdsInDb(user.getId()));

        friendsToDeleteFromDb.removeAll(user.getFriendIds());

        String deleteFriendsSql = "delete from friends where user_id = ? and friend_id = ?";

        List<Object[]> batchArgsList = new ArrayList<>();

        for (Integer friendId : friendsToDeleteFromDb) {
            Object[] objectArray = {user.getId(), friendId};
            batchArgsList.add(objectArray);
        }

        jdbcTemplate.batchUpdate(deleteFriendsSql, batchArgsList);

        Set<Integer> friendsAddToDb = new HashSet<>(user.getFriendIds());
        friendsAddToDb.removeAll(getFriendIdsInDb(user.getId()));

        String insertFriendsSql = "insert into friends (user_id, friend_id) VALUES (?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();

        for (Integer friendId : friendsAddToDb) {
            Object[] objectArray = {user.getId(), friendId};
            batchArgs.add(objectArray);
        }

        jdbcTemplate.batchUpdate(insertFriendsSql, batchArgs);

        log.info("В базе данных обновлен пользователь: {}", user);

        return user;
    }

    @Override
    public void deleteUser(int userId) {
        getUserById(userId);

        String deleteFriendsSql = "delete from friends where user_id = ? OR friend_id = ?";
        jdbcTemplate.update(deleteFriendsSql, userId, userId);

        String deleteUserSql = "delete from users where user_id = ?";
        jdbcTemplate.update(deleteUserSql, userId);

        log.info("Из базы данных удален пользователь с id: {}", userId);
    }

    @Override
    public void addFriend(int userId, int otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);

        if (!getFriendIdsInDb(userId).contains(otherUserId)) {
            String sql = "insert into friends (user_id, friend_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, userId, otherUserId);
        }
    }

    @Override
    public void removeFriend(int userId, int otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);

        if (getFriendIdsInDb(userId).contains(otherUserId)) {
            String sql = "delete from friends where user_id = ? and friend_id = ?";
            jdbcTemplate.update(sql, userId, otherUserId);
        }
    }

    @Override
    public List<User> getUserFriends(int userId) {
        String sql = "select * from users as u join friends as f on u.user_id = f.friend_id " +
                "where f.user_Id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);
        String sql = "select * from users as u join friends as f on u.user_id = f.friend_id " +
                "where f.user_Id = ? and f.friend_id in (select friend_id from friends where user_id = ?)";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherUserId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        String email = rs.getString("email");
        Set<Integer> friendIds = getFriendIdsInDb(id);
        return User.builder().id(id).login(login).name(name).email(email).birthday(birthday).friendIds(friendIds)
                .build();
    }

    private void replaceNameWithLoginIfNameIsEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private Set<Integer> getFriendIdsInDb(int id) {
        String sql = "select * from friends where user_id = ?";
        List<Integer> friendIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"),
                id);
        return new HashSet<>(friendIds);
    }
}
