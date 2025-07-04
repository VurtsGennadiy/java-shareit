package ru.practicum.shareit.exception;

import lombok.Getter;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.user.model.User;

@Getter
public class BookingCreateException extends RuntimeException {
    private final BookingCreateDto bookingCreateDto;
    private final User booker;

    public BookingCreateException(BookingCreateDto bookingCreateDto, User booker, String message) {
        super(message);
        this.bookingCreateDto = bookingCreateDto;
        this.booker = booker;
    }
}
