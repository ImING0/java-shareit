package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.impl.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

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
    private BookingMapper bookingMapper;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemService itemService;

    @Test
    void create_WhenValid_ReturnItem() {
    }

    @Test
    void update() {
    }

    @Test
    void getById() {
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
}