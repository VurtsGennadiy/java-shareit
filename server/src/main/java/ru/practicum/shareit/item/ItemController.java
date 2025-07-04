package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.createNewItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemExtendDto get(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable long itemId,
                          @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping
    public Collection<ItemExtendDto> getUserItems(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchByText(text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long itemId,
                       @RequestHeader(USER_ID_HEADER) long userId) {
        itemService.deleteItem(itemId, userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestHeader(USER_ID_HEADER) long userId,
                                 @RequestBody CommentCreateDto dto) {
        return itemService.addComment(dto, itemId, userId);
    }
}
