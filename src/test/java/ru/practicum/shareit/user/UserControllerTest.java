package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void addNewUser() throws Exception {
        UserDto userDto = createUserDto();
        when(userService.addNewUser(userDto)).thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString((userDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect((jsonPath("$.name", is(userDto.getName()))))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDto = createUserDto();
        when(userService.updateUser(userDto, 1L)).thenReturn(userDto);
        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString((userDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect((jsonPath("$.name", is(userDto.getName()))))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getAllUsers() throws Exception {
        UserDto userDto1 = createUserDto();
        UserDto userDto2 = createUserDto();
        userDto2.setId(2L);
        List<UserDto> users = List.of(userDto1, userDto2);
        when(userService.getAllUsers()).thenReturn(users);
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect((jsonPath("$[0].name", is(userDto1.getName()))))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail())));
    }

    @Test
    void getUser() throws Exception {
        UserDto userDto = createUserDto();
        when(userService.getUser(1L)).thenReturn(userDto);
        mvc.perform(get("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect((jsonPath("$.name", is(userDto.getName()))))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private UserDto createUserDto() {
        UserDto userDto = generator.nextObject(UserDto.class);
        userDto.setId(1L);
        String email = userDto.getEmail() + "@mail.ru";
        userDto.setEmail(email);
        return userDto;
    }
}