package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemResponse {
    private Long id;
    private String name;
    private Long ownerId;
}
