package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStateFilter;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createNewBooking(@RequestBody @Valid BookingCreateDto dto,
                                       @RequestHeader(USER_ID_HEADER) long bookerId) {
        return bookingService.createNewBooking(dto, bookerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                 @PathVariable long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestHeader(USER_ID_HEADER) long userId,
                                     @RequestParam boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping
    public List<BookingDto> getBookerBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                              @RequestParam(required = false, defaultValue = "ALL") BookingStateFilter state) {
        return bookingService.getBookerBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(USER_ID_HEADER) long ownerId,
                                             @RequestParam(required = false, defaultValue = "ALL") BookingStateFilter state) {
        return bookingService.getOwnerBookings(ownerId, state);
    }
}
