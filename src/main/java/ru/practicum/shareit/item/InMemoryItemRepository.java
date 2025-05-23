package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import java.util.*;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Item create(Item item) {
        Long id = ++idCounter;
        item.setId(id);
        items.put(id, item);
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
        return items.put(item.getId(), item);
    }

    @Override
    public Collection<Item> searchByText(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().contains(text)
                        || item.getDescription().contains(text))
                        && item.getIsAvailable())
                .toList();
    }
}
