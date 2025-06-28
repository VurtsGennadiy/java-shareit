package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final UserDto userDto = new UserDto(1L, "user_name", "user@email");

    @SneakyThrows
    @Test
    void create() {
        UserDto createDto = new UserDto(null, "user_name", "user@email");
        when(userService.createNewUser(createDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(createDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).createNewUser(createDto);
    }

    @SneakyThrows
    @Test
    void create_whenEmailDuplicate_thenStatusConflict() {
        UserDto createDto = new UserDto(null, "user_name", "user@email");
        when(userService.createNewUser(createDto))
                .thenThrow(new DataIntegrityViolationException("uq_user_email"));

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Пользователь с таким email уже существует"));

        verify(userService, times(1)).createNewUser(createDto);
    }

    @Test
    void getUser() throws Exception {
        Long userId = userDto.getId();
        when(userService.getUser(userId)).thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).getUser(userId);
    }

    @SneakyThrows
    @Test
    void update() {
        UserDto updateDto = new UserDto(null, "updated_user_name", null);
        UserDto expected = new UserDto(1L, "updated_user_name", "user@email");
        Long userId = userDto.getId();
        when(userService.updateUser(updateDto, userId)).thenReturn(expected);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.email").value(expected.getEmail()));

        verify(userService, times(1)).updateUser(updateDto, userId);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        Long userId = userDto.getId();
        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(userService, times(1)).deleteUser(userId);
    }
}