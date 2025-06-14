package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking toBooking(BookingCreateDto dto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStatus(Booking.Status.WAITING);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setStart(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME));
        dto.setEnd(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));
        dto.setStatus(booking.getStatus());
        dto.setBooker(UserMapper.mapToDto(booking.getBooker()));
        dto.setItem(ItemMapper.mapToDto(booking.getItem()));
        dto.setId(booking.getId());
        return dto;
    }

    public static List<BookingDto> toDto(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }
}
