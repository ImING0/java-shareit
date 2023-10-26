package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface IBookingService {
    BookingDtoOut create(Long userId,
                         BookingDtoIn bookingDtoIn);

    BookingDtoOut update(Long bookingId,
                         Boolean approved,
                         Long userId);

    BookingDtoOut getByBookingIdAndUserId(Long bookingId,
                                          Long userId);

    List<BookingDtoOut> getAllBookingsForCurrentUserId(Long userId,
                                                       State state);

    List<BookingDtoOut> getAllItemBookingsForOwnerId(Long userId,
                                                     State state);
}
