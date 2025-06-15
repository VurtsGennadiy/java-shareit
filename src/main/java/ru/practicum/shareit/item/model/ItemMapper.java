package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item toItem(ItemCreateDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    public static List<ItemDto> toDto(Collection<Item> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(ItemMapper::toDto)
                .toList();
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
        updatedItem.setOwner(item.getOwner());
        return updatedItem;
    }

    public static ItemExtendDto toExtendDto(Item item, List<Comment> comments, Booking next, Booking last) {
        ItemExtendDto dto = new ItemExtendDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setComments(CommentMapper.toDto(comments));
        dto.setNextBooking(next == null ? null : next.getStart().format(DateTimeFormatter.ISO_DATE_TIME));
        dto.setLastBooking(last == null ? null : last.getStart().format(DateTimeFormatter.ISO_DATE_TIME));
        return dto;
    }
}
