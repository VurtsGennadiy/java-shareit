package ru.practicum.shareit.item.model;

import org.mapstruct.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;


@Mapper(uses = {CommentMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    Item toItem(ItemCreateDto dto, User owner);

    ItemDto toDto(Item item);

    List<ItemDto> toDto(Collection<Item> items);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateItem(@MappingTarget Item item, ItemUpdateDto dto);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "nextBooking", source = "next.start")
    @Mapping(target = "lastBooking", source = "last.start")
    ItemExtendDto toExtendDto(Item item, List<Comment> comments, Booking next, Booking last);
}
