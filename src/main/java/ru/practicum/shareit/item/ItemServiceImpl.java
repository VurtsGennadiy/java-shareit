package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto createNewItem(ItemCreateDto newItem, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
        Item item = ItemMapper.mapToItem(newItem);
        item.setOwner(user);
        item = itemRepository.save(item);
        return ItemMapper.mapToDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemUpdateDto updatedItem, long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " не существует"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
        if (!item.getOwner().equals(user)) {
            throw new NotFoundException("Пользователь id = " + userId + " не является владельцем item id = " + item.getId());
        }
        item = ItemMapper.itemWithUpdatedFields(item, updatedItem);
        item = itemRepository.save(item);
        return ItemMapper.mapToDto(item);
    }

    @Override
    public ItemExtendDto getItem(long itemId) {
        Item item = getItemOrElseThrow(itemId);
        List<Comment> comments = commentRepository.findByItem(item);
        return ItemMapper.toExtendDto(item, comments, null, null);
    }

    @Override
    public Collection<ItemExtendDto> getUserItems(long userId) {
        getUserOrElseThrow(userId);
        List<Item> items = itemRepository.findByOwner_Id(userId);

        // получаем следующее и последнее бронирование для всех item пользователя
       // List<Booking> futureBookings = bookingRepository.findFutureBookingByItemOwner_Id(userId);
        List<Booking> futureBookings = bookingRepository.findFutureBookingByItems(items);
        Map<Item, List<Booking>> itemsBookings = futureBookings.stream().collect(Collectors.groupingBy(Booking::getItem));
        Map<Item, Booking> itemsNextBooking = new HashMap<>();
        Map<Item, Booking> itemsLastBooking = new HashMap<>();
        itemsBookings.forEach((item, booking) -> {
            Booking next = booking.stream().min(Comparator.comparing(Booking::getStart)).orElse(null);
            Booking last = booking.stream().max(Comparator.comparing(Booking::getStart)).orElse(null);
            itemsNextBooking.put(item, next);
            itemsLastBooking.put(item, last);
        });

        // получаем комментарии для всех item пользователя
        List<Comment> itemsComments = commentRepository.findByItemIn(items);
        Map<Item, List<Comment>> itemsCommentsMap = itemsComments.stream().collect(Collectors.groupingBy(Comment::getItem));

        return items.stream().map(item -> ItemMapper.toExtendDto(
                item,
                itemsCommentsMap.get(item),
                itemsNextBooking.get(item),
                itemsLastBooking.get(item)))
                .toList();
    }

    @Override
    public Collection<ItemDto> searchByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        HashSet<Item> findItems = new HashSet<>();
        findItems.addAll(itemRepository.findItemsByNameContainingIgnoreCaseAndAvailableTrue(text));
        findItems.addAll(itemRepository.findItemsByDescriptionContainingIgnoreCaseAndAvailableTrue(text));
        return ItemMapper.mapToDto(findItems);
    }

    @Override
    @Transactional
    public void deleteItem(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " не существует"));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Пользователь id = " + userId + " не является владельцем item id = " + item.getId());
        }
        itemRepository.delete(item);
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentCreateDto commentCreateDto, long itemId, long userId) {
        Item item = getItemOrElseThrow(itemId);
        User user = getUserOrElseThrow(userId);
        List<Booking> bookings = bookingRepository.findCompletedBookingsByItemIdAndUserId(itemId, userId);
        if (bookings.isEmpty()) {
            throw new CommentException("Невозможно оставить комментарий: пользователь id = "
                    + userId + " не арендовал item id = " + itemId);
        }
        Comment comment = CommentMapper.toComment(commentCreateDto, item, user);
        commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }

    private Item getItemOrElseThrow(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " не существует"));
    }

    private User getUserOrElseThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
    }
}
