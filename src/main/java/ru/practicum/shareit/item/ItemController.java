package ru.practicum.shareit.item;

import java.util.Collection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody @Valid ItemCreateDto newItem,
                          @RequestHeader(USER_ID_HEADER) @Positive long userId) {
        return itemService.createNewItem(newItem, userId);
    }

    @GetMapping("/{itemId}")
    public ItemExtendDto get(@PathVariable @Positive long itemId) {
        return itemService.getItem(itemId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestBody @Valid ItemUpdateDto updatedItem,
                          @PathVariable @Positive long itemId,
                          @RequestHeader(USER_ID_HEADER) @Positive long userId) {
        return itemService.updateItem(updatedItem, itemId, userId);
    }

    @GetMapping
    public Collection<ItemExtendDto> getUserItems(@RequestHeader(USER_ID_HEADER) @Positive long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchByText(text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive long itemId,
                       @RequestHeader(USER_ID_HEADER) @Positive long userId) {
        itemService.deleteItem(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable @Positive long itemId,
                                 @RequestHeader(USER_ID_HEADER) @Positive long userId,
                                 @RequestBody @Valid CommentCreateDto dto) {
        return itemService.addComment(dto, itemId, userId);
    }
}
