package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.impl.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserMapper userMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemService itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Test
    void create_WhenValid_ReturnItem() {
        Long userId = 1L;
        ItemDto itemDtoToSave = ItemDto.builder()
                .name("Клей Нюхательный")
                .description("Подходит чтобы хорошенько откиснуть")
                .available(true)
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemMapper.toItem(itemDtoToSave)).thenCallRealMethod();

        itemService.create(userId, itemDtoToSave);

        verify(itemRepository).save(itemArgumentCaptor.capture());

        Item actualSavedItemDto = itemArgumentCaptor.getValue();

        assertEquals(itemDtoToSave.getName(), actualSavedItemDto.getName());
        assertEquals(itemDtoToSave.getDescription(), actualSavedItemDto.getDescription());
        assertEquals(itemDtoToSave.getAvailable(), actualSavedItemDto.getAvailable());
        assertEquals(userId, actualSavedItemDto.getOwner());

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRepository, times(1)).save(actualSavedItemDto);
    }

    @Test
    void create_WhenUserNotFound_ThrowResourceNotFoundException() {
        Long userId = 1L;
        ItemDto itemDtoToSave = ItemDto.builder()
                .name("Клей Нюхательный")
                .description("Подходит чтобы хорошенько откиснуть")
                .available(true)
                .build();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> itemService.create(userId, itemDtoToSave));

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_WhenValid_ReturnUpdated() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDtoToUpdate = ItemDto.builder()
                .name("Клей Нюхательный СИЛЬНЫЙ")
                .description("Подходит чтобы хорошенько откиснуть (может навсегда)")
                .available(true)
                .build();
        Item existingItem = Item.builder()
                .id(itemId)
                .name("Клей Нюхательный")
                .description("Подходит чтобы хорошенько откиснуть")
                .available(true)
                .build();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(existingItem));

        itemService.update(userId, itemId, itemDtoToUpdate);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item actualUpdatedItem = itemArgumentCaptor.getValue();
        assertEquals(itemDtoToUpdate.getName(), actualUpdatedItem.getName());
        assertEquals(itemDtoToUpdate.getDescription(), actualUpdatedItem.getDescription());
        assertEquals(itemDtoToUpdate.getAvailable(), actualUpdatedItem.getAvailable());

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(actualUpdatedItem);
    }

    @Test
    void update_WhenUserNotFound_ThrowResourceNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDtoToUpdate = ItemDto.builder()
                .name("Клей Нюхательный СИЛЬНЫЙ")
                .description("Подходит чтобы хорошенько откиснуть (может навсегда)")
                .available(true)
                .build();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> itemService.update(userId, itemId, itemDtoToUpdate));

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_WhenAllFieldsAreNull_ThrowBadRequestException() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDtoToUpdate = ItemDto.builder()
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> itemService.update(userId, itemId, itemDtoToUpdate));

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getById() {
        /*Чтобы реализовать тест нужно:
         * Создать предмет класса Item со всеми заполенными полями (кромме букингов)
         * Создать last и next Booking, для установления в конечный Dto
         * Создать 2 комментария класса Comment*/
        Long itemId = 1L;
        Long userId = 1L;
        Item existingItem = Item.builder()
                .id(itemId)
                .owner(userId)
                .name("Пулемет")
                .description("Мощный пулемет, чтобы косить зомби")
                .available(true)
                .build();

    }

    @Test
    void getAllOwnerItemsByOwnerId() {
    }

    @Test
    void search() {
    }

    @Test
    void addComment() {
    }

    private Booking setLastBooking(Item existingItem) {
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.now()
                        .minusDays(4))
                .end(LocalDateTime.now()
                        .minusDays(1))
                .item(existingItem)
                .booker(User.builder().id(33L).name("Вася").email("vasya@mail.ru")
                        .build()).status(Status.APPROVED).build();
    }

    private Booking setNextBooking(Item existingItem) {
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.now()
                        .plusDays(1))
                .end(LocalDateTime.now()
                        .plusDays(3))
                .item(existingItem)
                .booker(User.builder().id(44L).name("Galya").email("galya@mail.ru")
                        .build()).status(Status.APPROVED).build();
    }

    private List<Comment> setComments(Item existingItem) {
        return List.of(Comment.builder()
                .id(1L).item(existingItem)
                .author(User.builder()
                        .id(33L)
                        .name("Вася").email("vasya@mail.ru")
                        .build())
                .text("Прекрасный товар!")
                .created(LocalDateTime.now())
                .build());
    }
}