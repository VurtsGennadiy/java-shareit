package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createNewItem(ItemCreateDto newItem, long userId);

    ItemDto updateItem(ItemUpdateDto updatedItem, long itemId, long userId);

    ItemDto getItem(long itemId);

    Collection<ItemDto> getUserItems(long userId);

    Collection<ItemDto> searchByText(String text);

    void deleteItem(long itemId, long userId);
}
