package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.IBookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDtoOut create(Long userId,
                                BookingDtoIn bookingDtoIn) {
        /*Не стал делать запросы в сервисы, потому что посчитал, что так будет быстрее,
         надежнее и
         проще. Решил взаимодействовать напрямую с хранилищами.*/
        User user = getUserOrThrowIfNotExist(userId);
        Item item = getItemOrThrowIfNotExist(bookingDtoIn.getItemId());
        validateBookingBeforeCreate(userId, bookingDtoIn);
        Booking booking = bookingMapper.toBooking(user, item, bookingDtoIn);
        booking.setStatus(Status.WAITING);
        return bookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut update(Long bookingId,
                                Boolean approved,
                                Long userId) {
        Booking booking = validateBookingDetails(bookingId, userId, ValidationType.UPDATE);
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOut getBookingById(Long bookingId,
                                        Long userId) {
        Booking booking = validateBookingDetails(bookingId, userId, ValidationType.GET);
        return bookingMapper.toBookingDtoOut(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllBookingsForCurrentUserId(Long userId,
                                                              State state) {
        throwIfUserNotFound(userId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByBookerId(userId,
                                LocalDateTime.now())
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId,
                                LocalDateTime.now())
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now())
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                                Status.WAITING)
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                                Status.REJECTED)
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllItemBookingsForOwnerId(Long userId,
                                                            State state) {
        throwIfUserNotFound(userId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByItemOwnerOrderByStartDesc(userId)
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByItemOwner(userId,
                                LocalDateTime.now())
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(userId,
                                LocalDateTime.now())
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByItemOwnerAndStartIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now())
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(userId,
                                Status.WAITING)
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(userId,
                                Status.REJECTED)
                        .stream()
                        .map(bookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
        }
        return null;
    }

    private void throwIfUserNotFound(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    String.format("User with id %d does not exist", userId));
        }
    }

    private Booking validateBookingDetails(Long bookingId,
                                           Long userId,
                                           ValidationType validationType) {
        Booking booking = getBookingOrThrowIfNotExist(bookingId);
        switch (validationType) {
            case UPDATE:
                if (!booking.getItem()
                        .getOwner()
                        .equals(userId)) {
                    throw new ResourceNotFoundException(
                            String.format("Booking with id %d does not belong to user with id %d",
                                    bookingId, userId));
                }
                if (!booking.getStatus()
                        .equals(Status.WAITING)) {
                    throw new BadRequestException(
                            String.format("Booking with id %d is not in waiting status",
                                    bookingId));
                }
                return booking;
            case GET:
                /*Проверяем, что запрашивает либо владелец вещи либо создатель брони*/
                Long bookerId = booking.getBooker()
                        .getId();
                Long ownerId = booking.getItem()
                        .getOwner();
                if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
                    throw new ResourceNotFoundException("You do not have access to this booking.");
                }
                return booking;
        }

        return null;
    }

    private void validateBookingBeforeCreate(Long userId,
                                             BookingDtoIn bookingDtoIn) {
        /*Пресекаем попытку бронирования недоступной вещи*/
        if (!getItemOrThrowIfNotExist(bookingDtoIn.getItemId()).getAvailable()) {
            throw new BadRequestException(
                    String.format("Item with id %d is not available for booking",
                            bookingDtoIn.getItemId()));
        }

        /*Пресекаем попытку забронировать свою вещь*/
        if (getItemOrThrowIfNotExist(bookingDtoIn.getItemId()).getOwner()
                .equals(userId)) {
            throw new ResourceNotFoundException(
                    String.format("Item with id %d is owned by user with id %d",
                            bookingDtoIn.getItemId(), userId));
        }

        /*Проверяем, что конец брони не раньше начала или старт равен концу*/
        if (bookingDtoIn.getStart()
                .isAfter(bookingDtoIn.getEnd())) {
            throw new BadRequestException(
                    String.format("Start date %s is after end date %s", bookingDtoIn.getStart(),
                            bookingDtoIn.getEnd()));
        } else if (bookingDtoIn.getStart()
                .equals(bookingDtoIn.getEnd())) {
            throw new BadRequestException(
                    String.format("Start date %s is equal to end date %s", bookingDtoIn.getStart(),
                            bookingDtoIn.getEnd()));
        }
    }

    private User getUserOrThrowIfNotExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id %d does not exist", userId)));
    }

    private Item getItemOrThrowIfNotExist(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Item with id %d does not exist", itemId)));
    }

    private Booking getBookingOrThrowIfNotExist(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Booking with id %d does not exist", bookingId)));
    }

    private enum ValidationType {
        UPDATE,
        GET
    }
}
