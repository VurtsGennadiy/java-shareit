package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createNewUser(UserDto dto);

    UserDto getUser(long userId);

    UserDto updateUser(UserDto dto, long userId);

    void deleteUser(long userId);
}
