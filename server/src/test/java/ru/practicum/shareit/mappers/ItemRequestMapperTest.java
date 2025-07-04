package ru.practicum.shareit.mappers;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest extends MapperTest {

    @Test
    void toItemResponse() {
        ItemShortDto itemShortDto = new ItemShortDto(1L, "item_name", 2L, 3L);
        ItemResponse itemResponse = itemRequestMapper.toItemResponse(itemShortDto);

        assertEquals(itemShortDto.getId(), itemResponse.getId());
        assertEquals(itemShortDto.getName(), itemResponse.getName());
        assertEquals(itemShortDto.getOwnerId(), itemResponse.getOwnerId());
    }
}
