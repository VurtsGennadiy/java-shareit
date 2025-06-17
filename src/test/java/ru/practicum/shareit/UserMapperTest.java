package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class UserMapperTest extends MapperTest {

    @Test
    void mapToUser() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("username");
        dto.setEmail("user@email.com");
        User user = userMapper.toUser(dto);

        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    void mapToUserDto() {
        User user = getUser();
        UserDto dto = userMapper.toDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void updateUser() {
        User user = getUser();
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("updated username");
        dto.setEmail(null);

        userMapper.updateUser(user, dto);
        assertEquals("updated username", user.getName());
        assertNotNull(user.getEmail());
    }
}
