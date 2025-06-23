package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingStateFilter;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl + API_PREFIX);
    }

    public ResponseEntity<Object> getBooking(long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> createNewBooking(BookingCreateDto bookingCreateDto, long bookerId) {
        return post("", bookerId, bookingCreateDto);
    }

    public ResponseEntity<Object> approveBooking(long bookingId, long userId, boolean approved) {
        Map<String, Object> params = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, params, null);
    }


    public ResponseEntity<Object> getBookerBookings(long userId, BookingStateFilter state) {
        Map<String, Object> params = Map.of("state", state);
        return get("?state={state}", userId, params);
    }

    public ResponseEntity<Object> getOwnerBookings(long ownerId, BookingStateFilter state) {
        Map<String, Object> params = Map.of("state", state);
        return get("/owner?state={state}", ownerId, params);
    }
}
