package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {
    @EqualsAndHashCode.Include
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
}
