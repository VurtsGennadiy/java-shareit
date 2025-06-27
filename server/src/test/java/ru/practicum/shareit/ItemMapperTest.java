package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest extends MapperTest {

    @Test
    void toDto() {
        Item item = getItem();
        ItemDto dto = itemMapper.toDto(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
    }

    @Test
    void toItem() {
        ItemDto dto = new ItemDto();
        dto.setName("item_name");
        dto.setDescription("item_description");
        dto.setAvailable(true);
        dto.setRequestId(1L);
        User owner = getUser();
        ItemRequest itemRequest = getItemRequest();
        Item item = itemMapper.toItem(dto, owner, itemRequest);

        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
        assertSame(owner, item.getOwner());
        assertSame(itemRequest, item.getItemRequest());
    }

    @Test
    void updateItem() {
        ItemDto dto = new ItemDto();
        String descriptionNew = "updated_item_description";
        dto.setDescription(descriptionNew);
        dto.setName(null);
        dto.setAvailable(false);

        Item item = getItem();
        String nameBefore = item.getName();
        Boolean availableBefore = item.getAvailable();

        itemMapper.updateItem(item, dto);
        assertEquals(nameBefore, item.getName(), "name не должен измениться");
        assertEquals(descriptionNew, item.getDescription(), "не обновилось поле description");
        assertTrue(availableBefore);
        assertFalse(item.getAvailable());
    }

    @Test
    void toExtendDto() {
        Item item = getItem();
        List<Comment> comments = List.of(getComment());
        Booking next = getBooking();
        Booking last = new Booking();
        last.setItem(item);
        last.setId(next.getId() + 1);
        last.setStart(next.getStart().plusSeconds(2));
        last.setEnd(last.getStart().plusSeconds(1));

        ItemExtendDto dto = itemMapper.toExtendDto(item, comments, next, last);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(comments.size(), dto.getComments().size());
        assertEquals(commentMapper.toDto(getComment()), dto.getComments().getFirst());
        assertEquals(next.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), dto.getNextBooking());
        assertEquals(last.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), dto.getLastBooking());
    }
}
