package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class UserMapperTest extends MapperTest {

    @Test
    void mapToUser() {
        UserDto dto = new UserDto();
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
        UserDto dto = new UserDto();
        dto.setName("updated username");
        dto.setEmail(null);

        userMapper.updateUser(user, dto);
        assertEquals("updated username", user.getName());
        assertNotNull(user.getEmail());
    }
}
