package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {
    ItemDto createNewItem(ItemCreateDto dto, long userId);

    ItemDto updateItem(ItemUpdateDto dto, long itemId, long userId);

    ItemExtendDto getItem(long itemId);

    Collection<ItemExtendDto> getUserItems(long userId);

    Collection<ItemDto> searchByText(String text);

    void deleteItem(long itemId, long userId);

    CommentDto addComment(CommentCreateDto commentCreateDto, long itemId, long userId);
}
