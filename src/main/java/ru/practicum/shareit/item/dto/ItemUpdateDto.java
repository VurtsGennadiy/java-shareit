package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validator.NotBlankOrNull;

@Data
public class ItemUpdateDto {
    @NotBlankOrNull
    private String name;
    @NotBlankOrNull
    private String description;
    private Boolean isAvailable;
}
