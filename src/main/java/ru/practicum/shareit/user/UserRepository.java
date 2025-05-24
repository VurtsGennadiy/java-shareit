package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    User create(User user);

    User update(User user);

    void addItemForUser(long userId, long itemId);

    Optional<User> getUser(long id);

    Set<Long> getUserItemsIds(long userId);

    void deleteUser(long userId);
}
