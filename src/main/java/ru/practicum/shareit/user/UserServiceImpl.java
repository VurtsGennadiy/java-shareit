package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

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

    @Override
    public UserDto updateUser(UserUpdateDto dto, long userId) {
        User user = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
        user = UserMapper.userWithUpdatedFields(user, dto);
        user = userRepository.update(user);
        return UserMapper.mapToDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
        userRepository.getUserItemsIds(userId).forEach(itemRepository::delete);
        userRepository.deleteUser(userId);
    }
}
