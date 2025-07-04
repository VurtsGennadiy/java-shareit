package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.errorhandle.ErrorResponse;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingCreateException;
import ru.practicum.shareit.item.model.CommentMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapperImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@Import({ItemMapperImpl.class, CommentMapperImpl.class, UserMapperImpl.class, BookingMapperImpl.class})
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService service;

    @Autowired
    private BookingMapperImpl bookingMapper;

    private User user1;
    private User user2;
    private Item item1;
    private Booking booking1;
    private BookingDto booking1Dto;

    @BeforeEach
    public void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("test user1")
                .email("test.user1@practicum")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("test user2")
                .email("test.user2@practicum")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .owner(user1)
                .available(Boolean.TRUE)
                .build();

        LocalDateTime booking1start = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        booking1 = Booking.builder()
                .id(1L)
                .item(item1)
                .booker(user2)
                .start(booking1start)
                .end(booking1start.plus(1, ChronoUnit.MILLIS))
                .status(Booking.Status.WAITING)
                .build();

        booking1Dto = bookingMapper.toDto(booking1);
    }

    @SneakyThrows
    @Test
    void createNewBooking() {
        BookingCreateDto createDto = new BookingCreateDto(item1.getId(), booking1.getStart(), booking1.getEnd());
        Long userId = user2.getId();
        when(service.createNewBooking(createDto, userId)).thenReturn(booking1Dto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(booking1Dto)));

        verify(service, times(1)).createNewBooking(createDto, userId);
    }

    @SneakyThrows
    @Test
    void createNewBooking_whenTimeNotValid_thenStatusBadRequest400() {
        BookingCreateDto createDto = new BookingCreateDto(item1.getId(), booking1.getEnd(), booking1.getStart());
        Long userId = user2.getId();

        BookingCreateException exception = new BookingCreateException(
                createDto, user2, "Время начала бронирования должно быть раньше времени окончания");
        ErrorResponse expected = new ErrorResponse(exception.getMessage());

        when(service.createNewBooking(createDto, userId)).thenThrow(exception);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        verify(service, times(1)).createNewBooking(createDto, userId);
    }

    @SneakyThrows
    @Test
    void approveBooking_whenUserIsOwner_thenStatusOk() {
        Long bookingId = booking1.getId();
        Long userId = user1.getId();
        BookingDto expected = booking1Dto;
        booking1Dto.setStatus(Booking.Status.APPROVED);
        when(service.approveBooking(bookingId, userId, true)).thenReturn(expected);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(booking1Dto)));

        verify(service, times(1)).approveBooking(bookingId, userId, true);
    }

    @SneakyThrows
    @Test
    void approveBooking_whenUserIsNotOwner_thenStatusForbidden403() {
        Long bookingId = booking1.getId();
        Long userId = user2.getId();

        AccessDeniedException exception = new AccessDeniedException(
                "Пользователю id = " + userId + " не доступно управление бронированием id = " + bookingId);

        ErrorResponse expected = new ErrorResponse(exception.getMessage());
        when(service.approveBooking(bookingId, userId, true)).thenThrow(exception);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        verify(service, times(1)).approveBooking(bookingId, userId, true);
    }
}