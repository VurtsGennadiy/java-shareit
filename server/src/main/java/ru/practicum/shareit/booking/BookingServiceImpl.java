package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStateFilter;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingCreateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createNewBooking(BookingCreateDto dto, long bookerId) {
        log.debug("Запрос на создание бронирования: item id = {}, booker id = {}, период с {} по {}",
                dto.getItemId(), bookerId, dto.getStart(), dto.getEnd());

        User booker = getUserOrElseThrow(bookerId);
        Item item = getItemOrElseThrow(dto.getItemId());
        if (item.getOwner().getId().equals(bookerId)) {
            throw new BookingCreateException(dto, booker, "Бронирование собственной вещи невозможно");
        }
        if (!item.getAvailable()) {
            throw new BookingCreateException(dto, booker, "Item не доступен для бронирования");
        }
        Booking booking = bookingMapper.toBooking(dto, item, booker);
        if (!validateBookingTime(booking)) {
            throw new BookingCreateException(dto, booker, "Время начала бронирования должно быть раньше времени окончания");
        }


        booking = bookingRepository.save(booking);
        log.info("Создано новое бронирование: item id = {}, booker id = {}, период с {} по {}",
                booking.getItem().getId(), bookerId, booking.getStart(), booking.getEnd());
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = getBookingOrElseThrow(bookingId);
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new AccessDeniedException(
                    "Пользователю id = " + userId + " не доступна информация о бронировании id = " + bookingId
            );
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, long ownerId, boolean approved) {
        log.debug("Запрос на подтверждение бронирования: booking_id = {}, owner_id = {}, approved = {}",
                bookingId, ownerId, approved);

        Booking booking = getBookingOrElseThrow(bookingId);
        if (ownerId != booking.getItem().getOwner().getId()) {
            throw new AccessDeniedException(
                    "Пользователю id = " + ownerId + " не доступно управление бронированием id = " + bookingId
            );
        }
        booking.setStatus(approved ? Booking.Status.APPROVED : Booking.Status.REJECTED);
        bookingRepository.save(booking);
        if (approved) {
            log.info("Бронирование booking_id = {} подтверждено пользователем owner_id = {}", bookingId, ownerId);
        } else {
            log.info("Бронирование booking_id = {} отклонено пользователем owner_id = {}", bookingId, ownerId);
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookerBookings(long bookerId, BookingStateFilter state) {
        User booker = getUserOrElseThrow(bookerId);
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerOrderByStartDesc(booker);
            case CURRENT -> bookingRepository.findCurrentBookingsByBooker_Id(bookerId);
            case PAST -> bookingRepository.findPastBookingByBooker_Id(bookerId);
            case FUTURE -> bookingRepository.findFutureBookingByBooker_Id(bookerId);
            case WAITING -> bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, Booking.Status.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, Booking.Status.REJECTED);
        };
        return bookingMapper.toDto(bookings);
    }

    @Override
    public List<BookingDto> getOwnerBookings(long ownerId, BookingStateFilter state) {
        User owner = getUserOrElseThrow(ownerId);
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItem_OwnerOrderByStartDesc(owner);
            case CURRENT -> bookingRepository.findCurrentBookingsByItemOwner_Id(ownerId);
            case PAST -> bookingRepository.findPastBookingsByItemOwner_Id(ownerId);
            case FUTURE -> bookingRepository.findFutureBookingByItemOwner_Id(ownerId);
            case WAITING -> bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(owner, Booking.Status.WAITING);
            case REJECTED -> bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(owner, Booking.Status.REJECTED);
        };
        return bookingMapper.toDto(bookings);
    }

    private Item getItemOrElseThrow(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id = " + itemId + " не существует"));
    }

    private Booking getBookingOrElseThrow(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не существует"));
    }

    private User getUserOrElseThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует"));
    }

    private boolean validateBookingTime(Booking booking) {
        return booking.getStart().isBefore(booking.getEnd());
    }
}
