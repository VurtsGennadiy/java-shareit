package ru.practicum.shareit.user;

import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    Optional<User> getUser(long id);
    void addItemForUser(long userId, long itemId);

    User create(User user);
    User update(User user);
    Set<Long> getUserItemsIds(long userId);
}
