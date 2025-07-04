package ru.practicum.shareit.booking;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStateFilter;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingCreateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class BookingServiceImplTest {
    private final BookingServiceImpl service;
    private final TestData testData;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;

    private static final String ROWS_COUNT_SQL = "SELECT COUNT(*) FROM bookings";

    private Item item1;
    private Item item2;
    private User user1;
    private User user2;
    private User user3;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Booking booking5;

    @PostConstruct
    private void setTestData() {
        item1 = testData.getItem1();
        item2 = testData.getItem2();
        user1 = testData.getUser1();
        user2 = testData.getUser2();
        user3 = testData.getUser3();
        booking1 = testData.getBooking1();
        booking2 = testData.getBooking2();
        booking3 = testData.getBooking3();
        booking4 = testData.getBooking4();
        booking5 = testData.getBooking5();
    }

    @Test
    void createNewBooking() {
        long bookerId = user2.getId();
        Long itemId = item1.getId();
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        BookingCreateDto createDto = new BookingCreateDto(itemId, start, start.plusSeconds(1));

        Long rowsBefore = jdbcTemplate.queryForObject(ROWS_COUNT_SQL, Long.class);
        BookingDto actual = service.createNewBooking(createDto, bookerId);
        Long rowsAfter = jdbcTemplate.queryForObject(ROWS_COUNT_SQL, Long.class);;
        assertEquals(rowsAfter, rowsBefore + 1);
        Map<String, Object> savedRow = jdbcTemplate.queryForMap("SELECT * FROM bookings WHERE id = ?", actual.getId());

        assertEquals(itemMapper.toDto(item1), actual.getItem());
        assertEquals(itemId, savedRow.get("item_id"));
        assertEquals(userMapper.toDto(user2), actual.getBooker());
        assertEquals(bookerId, savedRow.get("user_id"));
        assertEquals(start, actual.getStart());
        assertEquals(start, ((Timestamp) savedRow.get("start_time")).toLocalDateTime());
        assertEquals(createDto.getEnd(), actual.getEnd());
        assertEquals(createDto.getEnd(),((Timestamp) savedRow.get("end_time")).toLocalDateTime());
        assertEquals(Booking.Status.WAITING, actual.getStatus());
        assertEquals(Booking.Status.WAITING.toString(), savedRow.get("status"));
    }

    @Test
    void createNewBooking_whenItemNotAvailable_thenThrowException() {
        long bookerId = user2.getId();
        Long itemId = item2.getId();
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        BookingCreateDto createDto = new BookingCreateDto(itemId, start, start.plusSeconds(1));

        Long rowsBefore = jdbcTemplate.queryForObject(ROWS_COUNT_SQL, Long.class);
        assertThrows(BookingCreateException.class, () -> service.createNewBooking(createDto, bookerId));
        Long rowsAfter = jdbcTemplate.queryForObject(ROWS_COUNT_SQL, Long.class);

        assertEquals(rowsAfter, rowsBefore);
    }

    @Test
    void createNewBooking_whenBookingTimeNotValid_thenThrowException() {
        long bookerId = user2.getId();
        Long itemId = item1.getId();
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        BookingCreateDto createDto = new BookingCreateDto(itemId, start, start.minusSeconds(1));

        Long rowsBefore = jdbcTemplate.queryForObject(ROWS_COUNT_SQL, Long.class);
        assertThrows(BookingCreateException.class, () -> service.createNewBooking(createDto, bookerId));
        Long rowsAfter = jdbcTemplate.queryForObject(ROWS_COUNT_SQL, Long.class);

        assertEquals(rowsAfter, rowsBefore);
    }

    @Test
    void createNewBooking_whenBookerIsItemOwner_thenThrowException() {
        long bookerId = user1.getId();
        Long itemId = item2.getId();
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        BookingCreateDto createDto = new BookingCreateDto(itemId, start, start.plusSeconds(1));

        Long rowsBefore = jdbcTemplate.queryForObject(ROWS_COUNT_SQL, Long.class);
        assertThrows(BookingCreateException.class, () -> service.createNewBooking(createDto, bookerId));
        Long rowsAfter = jdbcTemplate.queryForObject(ROWS_COUNT_SQL, Long.class);

        assertEquals(rowsAfter, rowsBefore);
    }

    @Test
    void getBooking_whenUserIsOwner_thenGet() {
        Long bookingId = booking1.getId();
        Long userId = user1.getId();

        BookingDto expected = bookingMapper.toDto(booking1);
        BookingDto actual = service.getBooking(bookingId, userId);

        assertEquals(expected, actual);
    }

    @Test
    void getBooking_whenUserIsBooker_thenGet() {
        Long bookingId = booking1.getId();
        Long userId = user2.getId();

        BookingDto expected = bookingMapper.toDto(booking1);
        BookingDto actual = service.getBooking(bookingId, userId);

        assertEquals(expected, actual);
    }

    @Test
    void getBooking_whenUserIsStranger_thenThrowException() {
        Long bookingId = booking1.getId();
        Long userId = user3.getId();

        assertThrows(AccessDeniedException.class, () -> service.getBooking(bookingId, userId));
    }

    @Test
    void approveBooking_whenApprovedTrue_thenStatusApproved() {
        long ownerId = user1.getId();
        long bookerId = user2.getId();
        Long itemId = item1.getId();
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        BookingCreateDto createDto = new BookingCreateDto(itemId, start, start.plusSeconds(1));
        BookingDto booking = service.createNewBooking(createDto, bookerId);

        assertEquals(Booking.Status.WAITING, booking.getStatus());
        BookingDto actual = service.approveBooking(booking.getId(), ownerId, true);
        em.flush();
        Map<String, Object> savedRow = jdbcTemplate.queryForMap("SELECT * FROM bookings WHERE id = ?", actual.getId());

        assertEquals(Booking.Status.APPROVED.toString(), savedRow.get("status"));
        assertEquals(Booking.Status.APPROVED, actual.getStatus());
    }

    @Test
    void approveBooking_whenApprovedFalse_thenStatusRejected() {
        long ownerId = user1.getId();
        long bookerId = user2.getId();
        Long itemId = item1.getId();
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        BookingCreateDto createDto = new BookingCreateDto(itemId, start, start.plusSeconds(1));
        BookingDto booking = service.createNewBooking(createDto, bookerId);

        assertEquals(Booking.Status.WAITING, booking.getStatus());
        BookingDto actual = service.approveBooking(booking.getId(), ownerId, false);
        em.flush();
        Map<String, Object> savedRow = jdbcTemplate.queryForMap("SELECT * FROM bookings WHERE id = ?", actual.getId());

        assertEquals(Booking.Status.REJECTED.toString(), savedRow.get("status"));
        assertEquals(Booking.Status.REJECTED, actual.getStatus());
    }

    @Test
    void approveBooking_whenUserIsNotOwner_thenThrowException() {
        long bookerId = user2.getId();
        Long itemId = item1.getId();
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        BookingCreateDto createDto = new BookingCreateDto(itemId, start, start.plusSeconds(1));
        BookingDto booking = service.createNewBooking(createDto, bookerId);

        assertEquals(Booking.Status.WAITING, booking.getStatus());
        assertThrows(AccessDeniedException.class, () -> service.approveBooking(booking.getId(), bookerId, true));
        Map<String, Object> savedRow = jdbcTemplate.queryForMap("SELECT * FROM bookings WHERE id = ?", booking.getId());

        assertEquals(Booking.Status.WAITING.toString(), savedRow.get("status"));
    }

    @Test
    void getBookerBookings_whenStatePast() {
        BookingStateFilter state = BookingStateFilter.PAST;
        Long bookerId = user2.getId();
        List<BookingDto> expected = bookingMapper.toDto(List.of(booking1));

        List<BookingDto> actual = service.getBookerBookings(bookerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getBookerBookings_whenStateFuture() {
        BookingStateFilter state = BookingStateFilter.FUTURE;
        Long bookerId = user2.getId();
        List<BookingDto> expected = bookingMapper.toDto(List.of(booking2));

        List<BookingDto> actual = service.getBookerBookings(bookerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getBookerBookings_whenStateRejected() {
        BookingStateFilter state = BookingStateFilter.REJECTED;
        Long bookerId = user2.getId();
        List<BookingDto> expected = bookingMapper.toDto(List.of(booking3));

        List<BookingDto> actual = service.getBookerBookings(bookerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getBookerBookings_whenStateWaiting() {
        BookingStateFilter state = BookingStateFilter.WAITING;
        Long bookerId = user2.getId();
        List<BookingDto> expected = bookingMapper.toDto(List.of(booking4));

        List<BookingDto> actual = service.getBookerBookings(bookerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getBookerBookings_whenStateCurrent() {
        BookingStateFilter state = BookingStateFilter.CURRENT;
        Long bookerId = user2.getId();
        List<BookingDto> expected = bookingMapper.toDto(List.of(booking5));

        List<BookingDto> actual = service.getBookerBookings(bookerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getBookerBookings_whenStateAll() {
        BookingStateFilter state = BookingStateFilter.ALL;
        Long bookerId = user2.getId();
        List<BookingDto> expected = bookingMapper.toDto(
                List.of(booking4, booking2, booking1, booking5, booking3));

        List<BookingDto> actual = service.getBookerBookings(bookerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getOwnerBookings_whenStatePast() {
        BookingStateFilter state = BookingStateFilter.PAST;
        Long ownerId = user1.getId();
        List<BookingDto> expected = bookingMapper.toDto(List.of(booking1));

        List<BookingDto> actual = service.getOwnerBookings(ownerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getOwnerBookings_whenStateFuture() {
        BookingStateFilter state = BookingStateFilter.FUTURE;
        Long ownerId = user1.getId();
        List<BookingDto> expected = bookingMapper.toDto(List.of(booking2));

        List<BookingDto> actual = service.getOwnerBookings(ownerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getOwnerBookings_whenStateRejected() {
        BookingStateFilter state = BookingStateFilter.REJECTED;
        Long ownerId = user1.getId();
        List<BookingDto> expected = bookingMapper.toDto(List.of(booking3));

        List<BookingDto> actual = service.getOwnerBookings(ownerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getOwnerBookings_whenStateWaiting() {
        BookingStateFilter state = BookingStateFilter.WAITING;
        Long ownerId = user1.getId();
        List<BookingDto> expected = bookingMapper.toDto(List.of(booking4));

        List<BookingDto> actual = service.getOwnerBookings(ownerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getOwnerBookings_whenStateCurrent() {
        BookingStateFilter state = BookingStateFilter.CURRENT;
        Long ownerId = user1.getId();
        List<BookingDto> expected = bookingMapper.toDto(List.of(booking5));

        List<BookingDto> actual = service.getOwnerBookings(ownerId, state);
        assertEquals(expected, actual);
    }

    @Test
    void getOwnerBookings_whenStateAll() {
        BookingStateFilter state = BookingStateFilter.ALL;
        Long ownerId = user1.getId();
        List<BookingDto> expected = bookingMapper.toDto(
                List.of(booking4, booking2, booking1, booking5, booking3));

        List<BookingDto> actual = service.getOwnerBookings(ownerId, state);
        assertEquals(expected, actual);
    }
}