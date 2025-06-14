package ru.practicum.shareit.exception;

import lombok.Getter;
import ru.practicum.shareit.item.model.Item;

@Getter
public class ItemNotAvailableException extends RuntimeException {
    private final Item item;

    public ItemNotAvailableException(Item item) {
        super("Item id = " + item.getId() + " не доступен для бронирования");
        this.item = item;
    }
}
