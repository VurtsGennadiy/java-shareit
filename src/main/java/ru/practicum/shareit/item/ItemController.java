package ru.practicum.shareit.item;

import java.util.Collection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestBody @Valid ItemCreateDto newItem,
                          @RequestHeader(USER_ID_HEADER) @Positive Long userId) {
        return itemService.createNewItem(newItem, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable @Positive long itemId) {
        return itemService.getItem(itemId);
    }

    @PutMapping("/{itemId}")
    public ItemDto update(@RequestBody @Valid ItemUpdateDto updatedItem,
                          @PathVariable @Positive long itemId,
                          @RequestHeader(USER_ID_HEADER) @Positive long userId) {
        return itemService.updateItem(updatedItem, itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> getUserItems(@RequestHeader(USER_ID_HEADER) @Positive long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchByText(text);
    }
}
