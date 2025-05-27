package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> usersEmail = new HashMap<>();
    private final Map<Long, Set<Long>> usersItemsIds = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addItemForUser(long userId, long itemId) {
        usersItemsIds.get(userId).add(itemId);
    }

    @Override
    public User create(User user) {
        checkEmailDuplicate(user);
        Long id = ++idCounter;
        user.setId(id);
        users.put(id, user);
        usersEmail.put(user.getEmail(), user);
        usersItemsIds.put(id, new HashSet<>());
        log.info("Создан новый пользователь id = {}", id);
        return user;
    }

    @Override
    public Set<Long> getUserItemsIds(long userId) {
        return usersItemsIds.get(userId);
    }

    @Override
    public User update(User user) {
        checkEmailDuplicate(user);
        Long userId = user.getId();
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь id = " + user.getId() + " не существует");
        }
        usersEmail.remove(users.get(userId).getEmail());
        usersEmail.put(user.getEmail(), user);
        users.put(user.getId(), user);
        log.info("Обновлены данные пользователя id = {}", userId);
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        usersItemsIds.remove(userId);
        usersEmail.remove(users.get(userId).getEmail());
        users.remove(userId);
        log.info("Удалён пользователь id = {}", userId);
    }

    private void checkEmailDuplicate(User user) {
        User found = usersEmail.get(user.getEmail());
        if (found != null && !found.equals(user)) {
            throw new DuplicateDataException("Адрес " + user.getEmail() + " уже занят");
        }
    }
}
