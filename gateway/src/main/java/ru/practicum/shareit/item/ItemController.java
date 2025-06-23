package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody @Valid ItemCreateDto itemCreateDto,
                                         @RequestHeader(USER_ID_HEADER) @Positive long userId) {
        return itemClient.createNewItem(itemCreateDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable @Positive long itemId) {
        return itemClient.getItem(itemId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable @Positive long itemId,
                                         @RequestHeader(USER_ID_HEADER) @Positive long userId,
                                         @RequestBody @Valid ItemUpdateDto itemUpdateDto) {
        return itemClient.updateItem(itemId, userId, itemUpdateDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive long itemId,
                       @RequestHeader(USER_ID_HEADER) @Positive long userId) {
        itemClient.deleteItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(USER_ID_HEADER) @Positive long userId) {
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.searchByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable @Positive long itemId,
                                 @RequestHeader(USER_ID_HEADER) @Positive long userId,
                                 @RequestBody @Valid CommentCreateDto commentCreateDto) {
        return itemClient.addComment(itemId, userId, commentCreateDto);
    }
}
