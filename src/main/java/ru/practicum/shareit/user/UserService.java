package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createNewUser(UserCreateDto newUser);
    UserDto getUser(long userId);
}
