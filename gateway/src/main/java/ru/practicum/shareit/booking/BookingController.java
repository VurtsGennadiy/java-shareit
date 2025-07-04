package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingStateFilter;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> createNewBooking(@RequestHeader(USER_ID_HEADER) @Positive long bookerId,
												   @RequestBody @Valid BookingCreateDto bookingCreateDto) {
		return bookingClient.createNewBooking(bookingCreateDto, bookerId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@PathVariable long bookingId,
											 @RequestHeader(USER_ID_HEADER) long userId) {
		return bookingClient.getBooking(bookingId, userId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@PathVariable @Positive long bookingId,
												 @RequestHeader(USER_ID_HEADER) @Positive long userId,
												 @RequestParam boolean approved) {
		return bookingClient.approveBooking(bookingId, userId, approved);
	}

	@GetMapping
	public ResponseEntity<Object> getBookerBookings(@RequestHeader(USER_ID_HEADER) @Positive long userId,
													@RequestParam(defaultValue = "ALL") BookingStateFilter state) {
		return bookingClient.getBookerBookings(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader(USER_ID_HEADER) @Positive long ownerId,
												   @RequestParam(defaultValue = "ALL") BookingStateFilter state) {
		return bookingClient.getOwnerBookings(ownerId, state);
	}
}
