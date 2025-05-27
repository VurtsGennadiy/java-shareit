package ru.practicum.shareit.user.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User mapToUser(UserCreateDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static User userWithUpdatedFields(User user, UserUpdateDto dto) {
        User updatedUser = new User();
        String name = dto.getName() != null ? dto.getName() : user.getName();
        String email = dto.getEmail() != null ? dto.getEmail() : user.getEmail();
        updatedUser.setId(user.getId());
        updatedUser.setName(name);
        updatedUser.setEmail(email);
        return updatedUser;
    }
}
