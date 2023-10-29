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

    /**
     * Получить бронирование по его id
     *
     * @param bookingId id бронирования
     * @param userId    id пользователя
     * @return
     */
    BookingDtoOut getBookingById(Long bookingId,
                                 Long userId);

    /**
     * Получить все бронирования для текущего пользователя по его id
     *
     * @param userId id пользователя
     * @param state  статус бронирования
     * @return список бронирований
     */
    List<BookingDtoOut> getAllBookingsForCurrentUserId(Long userId,
                                                       State state);

    /**
     * Получить список бронирований для всех вещей текущего пользователя (их владельца)
     *
     * @param userId id владельца вещей
     * @param state  статус бронирования
     * @return список бронирований
     */
    List<BookingDtoOut> getAllItemBookingsForOwnerId(Long userId,
                                                     State state);
}
