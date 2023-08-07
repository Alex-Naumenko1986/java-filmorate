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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebMvcTest
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private static ObjectMapper mapper;
	private User user;

	@BeforeAll
	static void initialize() {
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
	}

	@BeforeEach
	void beforeEach() {
		user = User.builder().email("naumenko@yandex.ru").login("Cartman").name("Alex")
				.birthday(LocalDate.of(1986, 7,3)).build();
	}

	@Test
	void addUserWithCorrectData() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.content(asJsonString(user))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("naumenko@yandex.ru"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.login").value("Cartman"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Alex"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1986-07-03"));

	}

	@Test
	void emptyAddRequest() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content("")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
	}

	@Test
	void addUserWithInvalidLogin() throws Exception {
		user.setLogin("I am");
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.ALL))
					.andExpect(status().isBadRequest());
	}

	@Test
	void addUserWithEmptyLogin() throws Exception {
		user.setLogin("");

		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.ALL))
					.andExpect(status().isBadRequest());
	}

	@Test
	void addUserWithEmptyEmail() throws Exception {
		user.setEmail("");

		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.ALL))
					.andExpect(status().isBadRequest());
	}

	@Test
	void addUserWithWrongEmail() throws Exception {
		user.setEmail("yandex-mail@");

		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.ALL))
					.andExpect(status().isBadRequest());
	}

	@Test
	void addUserWithEmptyName() throws Exception {
		user.setName("");

		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Cartman"));
	}

	@Test
	void addUserWithWrongBirthday() throws Exception {
		user.setBirthday(LocalDate.of(2023, 8,15));

		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.ALL))
					.andExpect(status().isBadRequest());
	}

	@Test
	void updateUserWithCorrectData() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

		user.setId(1);
		user.setLogin("Eric");
		user.setEmail("mail@yandex.ru");

		this.mockMvc.perform(MockMvcRequestBuilders
					.put("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
					.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mail@yandex.ru"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.login").value("Eric"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Alex"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1986-07-03"));
	}

	@Test
	void emptyUpdateRequest() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

		this.mockMvc.perform(MockMvcRequestBuilders
					.put("/users")
					.content("")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
	}

	@Test
	void updateUserWithWrongLogin() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

		user.setId(1);
		user.setLogin("I am");

		this.mockMvc.perform(MockMvcRequestBuilders
					.put("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
	}

	@Test
	void updateUserWithEmptyLogin() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

		user.setId(1);
		user.setLogin("");

		this.mockMvc.perform(MockMvcRequestBuilders
					.put("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
	}

	@Test
	void updateUserWithEmptyEmail() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

		user.setId(1);
		user.setEmail("");

		this.mockMvc.perform(MockMvcRequestBuilders
					.put("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
	}

	@Test
	void updateUserWithWrongEmail() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

		user.setId(1);
		user.setEmail("mail-yandex@");

		this.mockMvc.perform(MockMvcRequestBuilders
					.put("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
	}

	@Test
	void updateUserWithEmptyName() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

		user.setId(1);
		user.setName("");

		this.mockMvc.perform(MockMvcRequestBuilders
					.put("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Cartman"));
	}

	@Test
	void updateUserWithWrongBirthday() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/users")
					.content(asJsonString(user))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

		user.setId(1);
		user.setBirthday(LocalDate.of(2023, 8, 20));

		this.mockMvc.perform(MockMvcRequestBuilders
					.put("/users")
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
