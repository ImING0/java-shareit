package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingService bookingService;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Test
    void create_WhenUserNotFound_ReturnResourceNotFound() {
        Long userId = 1L;
        Long itemId = 1L;
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now()
                        .plusDays(1))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.create(userId, bookingDtoIn));
    }

    @Test
    void create_WhenValidData_ReturnBookingDtoOut() {
        Long bookerId = 1L;
        Long itemId = 1L;
        User booker = User.builder()
                .id(1L)
                .name("Alex")
                .email("firstUser@gmail.com")
                .build();
        Item existingItem = Item.builder()
                .id(itemId)
                .owner(2L)
                .name("Клей Нюхательный")
                .description("Подходит чтобы хорошенько откиснуть")
                .available(true)
                .build();
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now()
                        .plusDays(1))
                .build();
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(bookingMapper.toBooking(booker, existingItem, bookingDtoIn)).thenCallRealMethod();

        bookingService.create(bookerId, bookingDtoIn);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());

        Booking actualSavedBooking = bookingArgumentCaptor.getValue();
        assertEquals(bookerId, actualSavedBooking.getBooker()
                .getId());
        assertEquals(itemId, actualSavedBooking.getItem()
                .getId());
        assertEquals(bookingDtoIn.getStart(), actualSavedBooking.getStart());
        assertEquals(bookingDtoIn.getEnd(), actualSavedBooking.getEnd());
        assertEquals(bookingDtoIn.getStatus(), actualSavedBooking.getStatus());
    }

    @Test
    void update_WhenValid_ReturnBookingDtoOut() {
        Long ownerId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        User booker = User.builder()
                .id(2L)
                .name("Alex")
                .email("firstUser@gmail.com")
                .build();
        Item existingItem = Item.builder()
                .id(itemId)
                .owner(ownerId)
                .name("Клей Нюхательный")
                .description("Подходит чтобы хорошенько откиснуть")
                .available(true)
                .build();
        Booking existingBooking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(existingItem)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now()
                        .plusDays(1))
                .status(Status.WAITING)
                .build();
        Boolean approved = true;

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        bookingService.update(bookingId, approved, ownerId);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());

        Booking updatedBooking = bookingArgumentCaptor.getValue();
        assertEquals(Status.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void getBookingById_WhenGetsOwner_ReturnBooking() {
        Long ownerId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        User booker = User.builder()
                .id(2L)
                .name("Alex")
                .email("firstUser@gmail.com")
                .build();
        Item existingItem = Item.builder()
                .id(itemId)
                .owner(ownerId)
                .name("Клей Нюхательный")
                .description("Подходит чтобы хорошенько откиснуть")
                .available(true)
                .build();
        Booking existingBooking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(existingItem)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now()
                        .plusDays(1))
                .status(Status.WAITING)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));

        bookingService.getBookingById(bookingId, ownerId);

        verify(bookingMapper).toBookingDtoOut(bookingArgumentCaptor.capture());
        Booking actualBooking = bookingArgumentCaptor.getValue();

        assertEquals(existingBooking.getId(), actualBooking.getId());
        assertEquals(existingBooking.getBooker()
                .getId(), actualBooking.getBooker()
                .getId());
    }
}