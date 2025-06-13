package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDto createNewItem(ItemCreateDto newItem, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
        Item item = ItemMapper.mapToItem(newItem);
        item.setOwner(user);
        item = itemRepository.save(item);
        return ItemMapper.mapToDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemUpdateDto updatedItem, long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " не существует"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
        if (!item.getOwner().equals(user)) {
            throw new NotFoundException("Пользователь id = " + userId + " не является владельцем item id = " + item.getId());
        }
        item = ItemMapper.itemWithUpdatedFields(item, updatedItem);
        item = itemRepository.save(item);
        return ItemMapper.mapToDto(item);
    }

    @Override
    public ItemDto getItem(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " не существует"));
        return ItemMapper.mapToDto(item);
    }

    @Override
    public Collection<ItemDto> getUserItems(long userId) {
        List<Item> userItems = itemRepository.findByOwner_Id(userId);
        return ItemMapper.mapToDto(userItems);
    }

    @Override
    public Collection<ItemDto> searchByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        HashSet<Item> findItems = new HashSet<>();
        findItems.addAll(itemRepository.findItemsByNameContainingIgnoreCaseAndAvailableTrue(text));
        findItems.addAll(itemRepository.findItemsByDescriptionContainingIgnoreCaseAndAvailableTrue(text));
        return ItemMapper.mapToDto(findItems);
    }

    @Override
    @Transactional
    public void deleteItem(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " не существует"));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Пользователь id = " + userId + " не является владельцем item id = " + item.getId());
        }
        itemRepository.delete(item);
    }
}
