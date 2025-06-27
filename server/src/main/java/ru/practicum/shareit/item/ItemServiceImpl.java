package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto createNewItem(ItemDto dto, long userId) {
        log.debug("Запрос на создание нового item: name = {}, owner_id = {}, request_id = {}",
                dto.getName(), userId, dto.getRequestId());

        User user = getUserOrElseThrow(userId);
        ItemRequest itemRequest = getItemRequestOrElseThrow(dto.getRequestId());
        Item item = itemMapper.toItem(dto, user, itemRequest);
        item = itemRepository.save(item);

        log.info("Создан новый item id = {}, name = {}, owner_id = {}, request_id = {}",
                item.getId(), item.getName(), userId, dto.getRequestId());

        return itemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto dto, long itemId, long userId) {
        log.debug("Запрос на обновление item: id = {}, name = {}, available = {}, owner_id = {}",
                itemId, dto.getName(), dto.getAvailable(), userId);

        Item item = getItemOrElseThrow(itemId);
        User user = getUserOrElseThrow(userId);
        if (!item.getOwner().equals(user)) {
            throw new NotFoundException("Пользователь id = " + userId + " не является владельцем item id = " + item.getId());
        }
        itemMapper.updateItem(item, dto);
        item = itemRepository.save(item);

        log.info("Обновлён item: id = {}, name = {}, available = {}, owner_id = {}",
                itemId, item.getName(), item.getAvailable(), userId);

        return itemMapper.toDto(item);
    }

    @Override
    public ItemExtendDto getItem(long itemId) {
        Item item = getItemOrElseThrow(itemId);
        List<Comment> comments = commentRepository.findByItem(item);
        return itemMapper.toExtendDto(item, comments, null, null);
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

        return items.stream().map(item -> itemMapper.toExtendDto(
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
        return itemMapper.toDto(findItems);
    }

    @Override
    @Transactional
    public void deleteItem(long itemId, long userId) {
        log.debug("Запрос на удаление item: id = {}, owner_id = {}", itemId, userId);

        Item item = getItemOrElseThrow(itemId);
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Пользователь id = " + userId + " не является владельцем item id = " + item.getId());
        }
        itemRepository.delete(item);

        log.info("Удалён item: id = {}", item.getId());
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentCreateDto commentCreateDto, long itemId, long userId) {
        log.debug("Запрос на добавление комментария к item_id = {}, author_id = {}", itemId, userId);

        Item item = getItemOrElseThrow(itemId);
        User user = getUserOrElseThrow(userId);
        List<Booking> bookings = bookingRepository.findCompletedBookingsByItemIdAndUserId(itemId, userId);
        if (bookings.isEmpty()) {
            throw new CommentException("Невозможно оставить комментарий: пользователь id = "
                    + userId + " не арендовал item id = " + itemId);
        }
        Comment comment = commentMapper.toComment(commentCreateDto, item, user);
        commentRepository.save(comment);

        log.info("Добавлен новый комментарий: id = {}, item_id = {}, author_id = {}", comment.getId(), itemId, userId);
        return commentMapper.toDto(comment);
    }

    private Item getItemOrElseThrow(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " не существует"));
    }

    private User getUserOrElseThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
    }

    private ItemRequest getItemRequestOrElseThrow(Long itemRequestId) {
        if (itemRequestId == null) return null;
        return itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest id = " + itemRequestId + " не существует"));
    }
}
