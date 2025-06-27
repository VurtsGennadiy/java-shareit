package ru.practicum.shareit;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingMapperImpl;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.model.UserMapperImpl;

import java.time.LocalDateTime;

public abstract class MapperTest {
    protected UserMapper userMapper = new UserMapperImpl();
    protected CommentMapper commentMapper = new CommentMapperImpl();
    protected ItemMapper itemMapper = new ItemMapperImpl(commentMapper);
    protected BookingMapper bookingMapper = new BookingMapperImpl(itemMapper, userMapper);
    protected ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();

    private final Item item;
    private final User user;
    private final User booker;
    private final Comment comment;
    private final Booking booking;
    private final ItemRequest itemRequest;

    protected Item getItem() {
        return item;
    }

    protected User getUser() {
        return user;
    }

    protected User getBooker() {
        return booker;
    }

    protected Comment getComment() {
        return comment;
    }

    protected Booking getBooking() {
        return booking;
    }

    protected ItemRequest getItemRequest() {
        return itemRequest;
    }

    public MapperTest() {
        user = new User();
        user.setId(1L);
        user.setName("username");
        user.setEmail("user@email");

        booker = new User();
        booker.setId(2L);
        booker.setName("booker_name");
        booker.setEmail("booker@email");

        item = new Item();
        item.setId(2L);
        item.setName("item_name");
        item.setDescription("item_description");
        item.setAvailable(true);
        item.setOwner(user);

        comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setText("item_comment_text");
        comment.setId(3L);

        booking = new Booking();
        booking.setId(4L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusSeconds(1));

        itemRequest = new ItemRequest();
        itemRequest.setId(5L);
        itemRequest.setDescription("item_request_description");
        itemRequest.setAuthor(booker);
    }
}
