package ru.practicum.shareit;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@Profile("test")
@RequiredArgsConstructor
@Slf4j
@Getter
public class TestData {
    private User user1;
    private User user2;
    private User user3;
    private ItemRequest request1;
    private ItemRequest request2;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Booking booking5;

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @PostConstruct
    @Transactional
    void saveData() throws InterruptedException {
        log.info("Сохранение тестовых данных в БД");

        user1 = User.builder()
                .name("test user1")
                .email("test.user1@practicum")
                .build();

        user2 = User.builder()
                .name("test user2")
                .email("test.user2@practicum")
                .build();

        user3 = User.builder()
                .name("test user3")
                .email("test.user3@practicum")
                .build();

        request1 = ItemRequest.builder()
                .description("request_description")
                .author(user2)
                .build();

        Thread.sleep(10); // для того чтобы у itemRequest было различное время создания

        request2 = ItemRequest.builder()
                .description("request2_description")
                .author(user2)
                .build();

        item1 = Item.builder()
                .name("item1")
                .description("description1")
                .owner(user1)
                .available(Boolean.TRUE)
                .build();

        item2 = Item.builder()
                .name("item2")
                .description("description2")
                .owner(user1)
                .available(Boolean.FALSE)
                .itemRequest(request1)
                .build();

        LocalDateTime booking1start = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        booking1 = Booking.builder()
                .item(item1)
                .booker(user2)
                .start(booking1start)
                .end(booking1start.plus(1, ChronoUnit.MILLIS))
                .status(Booking.Status.APPROVED)
                .build();

        LocalDateTime booking2start = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MILLIS);
        booking2 = Booking.builder()
                .item(item2)
                .booker(user2)
                .start(booking2start)
                .end(booking2start.plusSeconds(1))
                .status(Booking.Status.APPROVED)
                .build();

        LocalDateTime booking3start = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.MILLIS);
        booking3 = Booking.builder()
                .item(item1)
                .booker(user2)
                .start(booking3start)
                .end(booking3start.plus(1, ChronoUnit.MILLIS))
                .status(Booking.Status.REJECTED)
                .build();

        LocalDateTime booking4start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS);
        booking4 = Booking.builder()
                .item(item1)
                .booker(user2)
                .start(booking4start)
                .end(booking4start.plus(1, ChronoUnit.MILLIS))
                .status(Booking.Status.WAITING)
                .build();

        LocalDateTime booking5start = LocalDateTime.now().minusSeconds(1).truncatedTo(ChronoUnit.MILLIS);
        booking5 = Booking.builder()
                .item(item1)
                .booker(user2)
                .start(booking5start)
                .end(booking5start.plusHours(1))
                .status(Booking.Status.APPROVED)
                .build();

        comment1 = Comment.builder()
                .item(item1)
                .author(user2)
                .text("comment1_text")
                .build();

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);
        request1 = itemRequestRepository.save(request1);
        request2 = itemRequestRepository.save(request2);
        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
        booking1 = bookingRepository.save(booking1);
        booking2 = bookingRepository.save(booking2);
        booking3 = bookingRepository.save(booking3);
        booking4 = bookingRepository.save(booking4);
        booking5 = bookingRepository.save(booking5);
        comment1 = commentRepository.save(comment1);
    }
}
