package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import java.util.*;

@Repository
@Slf4j
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Item create(Item item) {
        Long id = ++idCounter;
        item.setId(id);
        items.put(id, item);
        log.info("Пользователь id = {} создал новый item id = {}", item.getOwnerId(), id);
        return item;
    }

    @Override
    public Optional<Item> get(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> get(Collection<Long> ids) {
        return ids.stream().map(items::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        log.info("Пользователь id = {} обновил item id = {}", item.getOwnerId(), item.getId());
        return item;
    }

    @Override
    public Collection<Item> searchByText(String text) {
        if (text.isBlank()) return Collections.emptyList();
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                        && item.getAvailable())
                .toList();
    }

    @Override
    public void delete(long id) {
        Item item = items.remove(id);
        log.info("Пользователь id = {} удалил item id = {}", item.getOwnerId(), id);
    }
}
