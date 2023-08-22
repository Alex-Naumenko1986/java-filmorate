package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder
public class User {
    private Integer id;
    @NotBlank(message = "E-mail не должен быть пустым")
    @Email(message = "Некорректный формат e-mail")
    private String email;
    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private final Set<Integer> friendIds = new HashSet<>();

    public boolean addFriend(int friendId) {
        return friendIds.add(friendId);
    }

    public boolean removeFriend(int friendId) {
        return friendIds.remove(friendId);
    }
}
