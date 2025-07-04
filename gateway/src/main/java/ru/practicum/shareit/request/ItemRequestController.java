package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createNewItemRequest(@RequestHeader(USER_ID_HEADER) @Positive long userId,
                                                       @RequestBody @Valid ItemRequestCreateDto dto) {
        return itemRequestClient.createNewItemRequest(userId, dto);
    }

    /**
     * получить список своих запросов вместе с ответами на них
     */
    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader(USER_ID_HEADER) @Positive long userId) {
        return itemRequestClient.getUserItemRequests(userId);
    }

    /**
     * получить список запросов других пользователей, исключая свои
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID_HEADER) @Positive long userId) {
        return itemRequestClient.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable @Positive long requestId) {
        return itemRequestClient.getItemRequest(requestId);
    }
}
