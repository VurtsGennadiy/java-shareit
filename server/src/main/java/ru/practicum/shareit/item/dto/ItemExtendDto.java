package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemExtendDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private String lastBooking;
    private String nextBooking;
}
