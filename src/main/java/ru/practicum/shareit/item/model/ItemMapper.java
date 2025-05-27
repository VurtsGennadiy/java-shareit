package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item mapToItem(ItemCreateDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static ItemDto mapToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    public static Item itemWithUpdatedFields(Item item, ItemUpdateDto dto) {
        Item updatedItem = new Item();
        String name = dto.getName() != null ? dto.getName() : item.getName();
        String description = dto.getDescription() != null ? dto.getDescription() : item.getDescription();
        Boolean available = dto.getAvailable() != null ? dto.getAvailable() : item.getAvailable();
        updatedItem.setName(name);
        updatedItem.setDescription(description);
        updatedItem.setAvailable(available);
        updatedItem.setId(item.getId());
        updatedItem.setOwnerId(item.getOwnerId());
        return updatedItem;
    }
}
