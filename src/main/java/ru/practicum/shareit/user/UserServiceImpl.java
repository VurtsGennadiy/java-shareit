package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public UserDto createNewUser(UserCreateDto newUser) {
        User user = UserMapper.mapToUser(newUser);
        user = userRepository.create(user);
        return UserMapper.mapToDto(user);
    }

    @Override
    public UserDto getUser(long userId) {
        User user = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
        return UserMapper.mapToDto(user);
    }
}
