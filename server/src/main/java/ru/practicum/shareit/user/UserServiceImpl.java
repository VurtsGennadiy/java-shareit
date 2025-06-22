package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createNewUser(UserCreateDto dto) {
        log.debug("Запрос на создание нового пользователя: email = {}", dto.getEmail());
        User user = userMapper.toUser(dto);
        user = userRepository.save(user);
        log.info("Создан новый пользователь id = {}, name = {}, email = {}", user.getId(), user.getName(), user.getEmail());
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUser(long userId) {
        User user = getUserOrElseThrow(userId);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserUpdateDto dto, long userId) {
        log.debug("Запрос на обновление пользователя user_id = {}", userId);
        User user = getUserOrElseThrow(userId);
        userMapper.updateUser(user, dto);
        user = userRepository.save(user);
        log.info("Обновлены данные пользователя id = {}, name = {}, email = {}", userId, user.getName(), user.getId());
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        log.debug("Запрос на удаление пользователя id = {}", userId);
        User user = getUserOrElseThrow(userId);
        userRepository.delete(user);
        log.info("Удален пользователь id = {}", userId);
    }

    private User getUserOrElseThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
    }
}
