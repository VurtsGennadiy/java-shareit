package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponses;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    ItemRequest toItemRequest(ItemRequestDto dto, User author);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    List<ItemRequestDto> toItemRequestDto(List<ItemRequest> itemRequests);

    ItemRequestWithResponses toItemRequestWithResponses(ItemRequestDto itemRequestDto, List<ItemResponse> items);

    ItemResponse toItemResponse(ItemShortDto itemShortDto);

    List<ItemResponse> toItemResponse(Collection<ItemShortDto> itemShortsDto);
}
