package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureTestDatabase
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    private static ObjectMapper mapper;
    private User user;

    private Integer addedUserId;

    @BeforeAll
    static void initialize() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @BeforeEach
    void beforeEach() {
        user = User.builder().email("naumenko@yandex.ru").login("Cartman").name("Alex")
                .birthday(LocalDate.of(1986, 7, 3)).build();
        addedUserId = userController.addUser(user).getId();
        user.setId(null);
    }

    @Test
    void shouldAddUserWhenDataIsValid() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("naumenko@yandex.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("Cartman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Alex"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1986-07-03"));
    }

    @Test
    void shouldReturnErrorWhenSendingEmptyAddRequest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnErrorWhenAddUserWithInvalidLogin() throws Exception {
        user.setLogin("I am");
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorWhenAddUserWithEmptyLogin() throws Exception {
        user.setLogin("");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorWhenAddUserWithEmptyEmail() throws Exception {
        user.setEmail("");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorWhenAddUserWithInvalidEmail() throws Exception {
        user.setEmail("yandex-mail@");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnLoginWhenAddUserWithEmptyName() throws Exception {
        user.setName("");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Cartman"));
    }

    @Test
    void shouldReturnErrorWhenAddUserWithInvalidBirthday() throws Exception {
        user.setBirthday(LocalDate.of(2023, 10, 30));

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateUserWithValidData() throws Exception {
        user.setId(addedUserId);
        user.setLogin("Eric");
        user.setEmail("mail@yandex.ru");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mail@yandex.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("Eric"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Alex"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1986-07-03"));
    }

    @Test
    void shouldReturnErrorWhenUpdateRequestIsEmpty() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnErrorWhenUpdateUserWithInvalidLogin() throws Exception {
        user.setId(addedUserId);
        user.setLogin("I am");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorWhenUpdateUserWithEmptyLogin() throws Exception {
        user.setId(addedUserId);
        user.setLogin("");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorWhenUpdateUserWithEmptyEmail() throws Exception {
        user.setId(addedUserId);
        user.setEmail("");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorWhenUpdateUserWithInvalidEmail() throws Exception {
        user.setId(addedUserId);
        user.setEmail("mail-yandex@");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnLoginWhenUpdateUserWithEmptyName() throws Exception {
        user.setId(addedUserId);
        user.setName("");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Cartman"));
    }

    @Test
    void shouldReturnErrorWhenUpdateUserWithInvalidBirthday() throws Exception {
        user.setId(addedUserId);
        user.setBirthday(LocalDate.of(2023, 10, 30));

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public String asJsonString(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
