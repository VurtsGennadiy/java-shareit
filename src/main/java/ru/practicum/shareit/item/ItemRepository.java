package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    Item update(Item item);

    Optional<Item> get(long id);

    Collection<Item> get(Collection<Long> ids);

    Collection<Item> searchByText(String text);

    void delete(long id);
}
