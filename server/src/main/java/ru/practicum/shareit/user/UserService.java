package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {
    UserDto createNewUser(UserCreateDto dto);

    UserDto getUser(long userId);

    UserDto updateUser(UserUpdateDto dto, long userId);

    void deleteUser(long userId);
}
