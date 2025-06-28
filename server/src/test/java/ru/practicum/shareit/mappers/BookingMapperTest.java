package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest extends MapperTest {
    @Test
    void toDto() {
        Booking booking = getBooking();
        BookingDto dto = bookingMapper.toDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getStatus(), dto.getStatus());
        assertEquals(itemMapper.toDto(booking.getItem()), dto.getItem());
        assertEquals(userMapper.toDto(booking.getBooker()), dto.getBooker());
    }

    @Test
    void toBooking() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusSeconds(1);
        BookingCreateDto dto = new BookingCreateDto();
        dto.setStart(start);
        dto.setEnd(end);
        Item item = getItem();
        User booker = getBooker();
        Booking booking = bookingMapper.toBooking(dto, item, booker);

        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(Booking.Status.WAITING, booking.getStatus());
    }
}
