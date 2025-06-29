package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
public class BookingDto {
    private Long id;
    private String start;
    private String end;
    private Booking.Status status;
    private ItemDto item;
    private UserDto booker;
}
