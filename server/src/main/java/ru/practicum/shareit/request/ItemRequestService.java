package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponses;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createNewRequest(ItemRequestDto dto, long userId);

    List<ItemRequestWithResponses> getUserRequests(long userId);

    List<ItemRequestDto> getOtherUsersRequests(long userId);

    ItemRequestWithResponses getRequest(long requestId);
}
