package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponses;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createNewItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                               @RequestHeader(USER_ID_HEADER) long userId) {
        return service.createNewRequest(itemRequestDto, userId);
    }

    /**
     * получить список своих запросов вместе с ответами на них
     */
    @GetMapping
    public List<ItemRequestWithResponses> getUserItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return service.getUserRequests(userId);
    }

    /**
     * получить список запросов других пользователей, исключая свои
     */
    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return service.getOtherUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithResponses getItemRequest(@PathVariable long requestId) {
        return service.getRequest(requestId);
    }
}
