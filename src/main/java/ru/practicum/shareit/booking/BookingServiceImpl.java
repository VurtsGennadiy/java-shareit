package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStateFilter;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    // TODO нельзя бронировать собственную вещь!
    public BookingDto createNewBooking(BookingCreateDto dto, long bookerId) {
        User booker = getUser(bookerId);
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item с id = " + dto.getItemId() + " не существует"));
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(item);
        }
        Booking booking = BookingMapper.toBooking(dto, item, booker);
        validateBookingTime(booking);
        booking = bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не существует"));
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new AccessDeniedException(
                    "Пользователю id = " + userId + " не доступна информация о бронировании id = " + bookingId
            );
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не существует"));

        if (ownerId != booking.getItem().getOwner().getId()) {
            throw new AccessDeniedException(
                    "Пользователю id = " + ownerId + " не доступно управление бронированием id = " + bookingId
            );
        }
        booking.setStatus(approved ? Booking.Status.APPROVED : Booking.Status.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookerBookings(long bookerId, BookingStateFilter state) {
        User booker = getUser(bookerId);
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerOrderByStartDesc(booker);
            case CURRENT -> bookingRepository.findCurrentBookingsByBooker_Id(bookerId);
            case PAST -> bookingRepository.findPastBookingByBooker_Id(bookerId);
            case FUTURE -> bookingRepository.findFutureBookingByBooker_Id(bookerId);
            case WAITING -> bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, Booking.Status.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, Booking.Status.REJECTED);
        };
        return BookingMapper.toDto(bookings);
    }

    @Override
    public List<BookingDto> getOwnerBookings(long ownerId, BookingStateFilter state) {
        User owner = getUser(ownerId);
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItem_OwnerOrderByStartDesc(owner);
            case CURRENT -> bookingRepository.findCurrentBookingsByItemOwner_Id(ownerId);
            case PAST -> bookingRepository.findPastBookingsByItemOwner_Id(ownerId);
            case FUTURE -> bookingRepository.findFutureBookingByItemOwner_Id(ownerId);
            case WAITING -> bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(owner, Booking.Status.WAITING);
            case REJECTED -> bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(owner, Booking.Status.REJECTED);
        };
        return BookingMapper.toDto(bookings);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует"));
    }

    private void validateBookingTime(Booking booking) {
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new RuntimeException("Начало аренды не может быть позднее окончания");
        }
    }
}
