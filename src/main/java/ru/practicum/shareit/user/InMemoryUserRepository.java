package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
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
        Long id = ++idCounter;
        user.setId(id);
        users.put(id, user);
        usersItemsIds.put(id, new HashSet<>());
        return user;
    }

    @Override
    public Set<Long> getUserItemsIds(long userId) {
        return usersItemsIds.get(userId);
    }

    @Override
    public User update(User user) {
       return users.put(user.getId(), user);
    }
}
