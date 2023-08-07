package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebMvcTest
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private static ObjectMapper mapper;
    private Film film;

    @BeforeAll
    static void initialize() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @BeforeEach
    void beforeEach() {
        film = Film.builder().name("Bad dog").description("Description")
                .releaseDate(LocalDate.of(1985, 8, 23)).duration(60).build();
    }

    @Test
    void addFilmWithCorrectData() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/films")
                .content(asJsonString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Bad dog"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("1985-08-23"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(60));

    }

    @Test
    void emptyAddRequest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content("")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmWithEmptyName() throws Exception {
        film.setName("");

        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmWithWrongDescription() throws Exception {
        film.setDescription("This is a very very long description. This film is about dog which always barks " +
                "and bites its neighbours. The dog likes to eat and to swim. And the dog also likes children. " +
                "The name of dog is Samuelson");

        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmWithWrongReleaseDate() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 12,27));

        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmWithZeroDuration() throws Exception {
        film.setDuration(0);

        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmWithCorrectData() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        film.setId(1);
        film.setName("Wall Street");
        film.setReleaseDate(LocalDate.of(2015, 5, 20));
        film.setDuration(120);

        this.mockMvc.perform(MockMvcRequestBuilders
                    .put("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Wall Street"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("2015-05-20"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.description").
                            value("Description"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(120));
    }

    @Test
    void emptyUpdateRequest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        this.mockMvc.perform(MockMvcRequestBuilders
                    .put("/films")
                    .content("")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmWithEmptyName() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        film.setId(1);
        film.setName("");

        this.mockMvc.perform(MockMvcRequestBuilders
                    .put("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmWithWrongDescription() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        film.setId(1);
        film.setDescription("This is a very very long description. This film is about dog which always barks " +
                "and bites its neighbours. The dog likes to eat and to swim. And the dog also likes children. " +
                "The name of dog is Samuelson");

        this.mockMvc.perform(MockMvcRequestBuilders
                    .put("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmWithWrongReleaseDate() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        film.setId(1);
        film.setReleaseDate(LocalDate.of(1895, 12,27));

        this.mockMvc.perform(MockMvcRequestBuilders
                    .put("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmWithZeroDuration() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/films")
                    .content(asJsonString(film))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        film.setId(1);
        film.setDuration(0);

        this.mockMvc.perform(MockMvcRequestBuilders
                    .put("/films")
                    .content(asJsonString(film))
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
