package ru.practicum.shareit.request.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private RequestMapper requestMapper;
    @InjectMocks
    private ItemRequestService itemRequestService;
    @Captor
    private ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;

    @Test
    void create_WhenUserNotFound_ThrowResourceNotFound() {
        Long userId = 1L;
        ItemRequestDtoIn itemRequestDtoIn = ItemRequestDtoIn.builder()
                .description("Нужно мыло, чтобы повеситься, срочно!")
                .requestorId(userId)
                .build();
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> itemRequestService.create(itemRequestDtoIn, userId));
    }

    @Test
    void create_WhenUserExists_ReturnItemRequestDtoOut() {
        Long userId = 1L;
        ItemRequestDtoIn itemRequestDtoIn = ItemRequestDtoIn.builder()
                .description("Нужно мыло, чтобы повеситься, срочно!")
                .requestorId(userId)
                .build();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestMapper.fromDtoIn(any(ItemRequestDtoIn.class))).thenCallRealMethod();

        itemRequestService.create(itemRequestDtoIn, userId);
        verify(itemRequestRepository).save(itemRequestArgumentCaptor.capture());

        ItemRequest itemRequest = itemRequestArgumentCaptor.getValue();
        assertEquals(itemRequestDtoIn.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDtoIn.getRequestorId(), itemRequest.getRequestor());
    }

    @Test
    void getById_WhenUserNotFound_ThrowResourceNotFound() {
        Long userId = 1L;
        Long requestId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> itemRequestService.getById(requestId, userId));
    }

    @Test
    void getById_WhenRequestNotFound_ThrowResourceNotFound() {
        Long userId = 1L;
        Long requestId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> itemRequestService.getById(requestId, userId));
    }

    @Test
    void getById_WhenRequestFound_ReturnItemRequestDtoOut() {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("Нужно мыло, чтобы повеситься, срочно!")
                .requestor(userId)
                .build();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(requestMapper.toDtoOut(any(ItemRequest.class))).thenCallRealMethod();

        ItemRequestDtoOut itemRequestDtoOut = itemRequestService.getById(requestId, userId);
        assertEquals(itemRequest.getDescription(), itemRequestDtoOut.getDescription());
        assertEquals(itemRequest.getRequestor(), itemRequestDtoOut.getRequestor());

        verify(itemRequestRepository).findById(requestId);
        verify(requestMapper).toDtoOut(itemRequest);
    }

    @Test
    void getAllByUserId_WhenUserNotFound_ThrowResourceNotFound() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> itemRequestService.getAllByUserId(userId));
    }

    @Test
    void getAllByUserId_WhenUserFound_ReturnListOfItemRequestDtoOut() {
        Long userId = 1L;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Нужно мыло, чтобы повеситься, срочно!")
                .requestor(userId)
                .build();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorOrderByCreatedDesc(userId)).thenReturn(
                List.of(itemRequest));
        when(requestMapper.toDtoOut(any(ItemRequest.class))).thenCallRealMethod();

        itemRequestService.getAllByUserId(userId);
        verify(itemRequestRepository).findAllByRequestorOrderByCreatedDesc(userId);
        verify(requestMapper).toDtoOut(itemRequest);
    }

    @Test
    void getAllFromOthers_WhenUserNotFound_ThrowResourceNotFound() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> itemRequestService.getAllFromOthers(userId, 0, 10));
    }
}