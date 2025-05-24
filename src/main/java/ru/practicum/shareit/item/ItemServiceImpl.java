package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createNewItem(ItemCreateDto newItem, long userId) {
        checkUserExists(userId);
        Item item = ItemMapper.mapToItem(newItem);
        item.setOwnerId(userId);
        item = itemRepository.create(item);
        userRepository.addItemForUser(userId, item.getId());
        return ItemMapper.mapToDto(item);
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto updatedItem, long itemId, long userId) {
        checkUserExists(userId);
        Item item = itemRepository.get(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " не существует"));
        checkUserIsItemOwner(item, userId);
        item = ItemMapper.itemWithUpdatedFields(item, updatedItem);
        item = itemRepository.update(item);
        return ItemMapper.mapToDto(item);
    }

    @Override
    public ItemDto getItem(long itemId) {
        Item item = itemRepository.get(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " не существует"));
        return ItemMapper.mapToDto(item);
    }

    @Override
    public Collection<ItemDto> getUserItems(long userId) {
        checkUserExists(userId);
        Set<Long> userItemsIds = userRepository.getUserItemsIds(userId);
        return itemRepository.get(userItemsIds).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchByText(String text) {
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public void deleteItem(long itemId, long userId) {
        Item item = itemRepository.get(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " не существует"));
        checkUserIsItemOwner(item, userId);
        itemRepository.delete(itemId);
    }

    private void checkUserExists(long userId) {
        userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
    }

    private void checkUserIsItemOwner(Item item, long userId) {
        if (item.getOwnerId() != userId) {
            throw new NotFoundException("Пользователь id = " + userId + " не является владельцем item id = " + item.getId());
        }
    }
}
