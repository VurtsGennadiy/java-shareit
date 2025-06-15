package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStateFilter;

import java.util.List;

public interface BookingService {
    BookingDto createNewBooking(BookingCreateDto dto, long bookerId);

    BookingDto getBooking(long bookingId, long userId);

    BookingDto approveBooking(long bookingId, long ownerId, boolean approved);

    List<BookingDto> getBookerBookings(long bookerId, BookingStateFilter state);

    List<BookingDto> getOwnerBookings(long ownerId, BookingStateFilter state);
}
