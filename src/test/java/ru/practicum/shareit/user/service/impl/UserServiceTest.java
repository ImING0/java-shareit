package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void create_WhenValid_ReturnUserDto() {
        UserDto userToSave = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("firstUser@gmail.com")
                .build();
        User userSaved = User.builder()
                .id(1L)
                .name("Alex")
                .email("firstUser@gmail.com")
                .build();
        when(userMapper.toUser(userToSave)).thenReturn(userSaved);
        when(userRepository.save(userSaved)).thenReturn(userSaved);
        when(userMapper.toUserDto(userSaved)).thenReturn(userToSave);

        UserDto userDtoOut = userService.create(userToSave);
        assertEquals(userToSave, userDtoOut);
        verify(userRepository).save(userSaved);
    }

    @Test
    void update_WhenDataValid_ReturnUpdatedUserDto() {
        Long userId = 1L;
        User existingUser = User.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();
        UserDto userToUpdate = UserDto.builder()
                .id(1L)
                .name("Alex Positive")
                .email("alexPositive@gmail.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        userService.update(userId, userToUpdate);
        verify(userRepository).save(userArgumentCaptor.capture());
        User updatedUser = userArgumentCaptor.getValue();

        assertEquals(userToUpdate.getId(), updatedUser.getId());
        assertEquals(userToUpdate.getName(), updatedUser.getName());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());

        InOrder inOrder = Mockito.inOrder(userRepository);
        inOrder.verify(userRepository)
                .findById(userId);
        inOrder.verify(userRepository)
                .save(existingUser);
    }

    @Test
    void update_WhenSameData_ReturnExistingUserDto() {
        Long userId = 1L;
        User existingUser = User.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();
        UserDto userToUpdate = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        userService.update(userId, userToUpdate);
        verify(userRepository).save(userArgumentCaptor.capture());

        User updatedUser = userArgumentCaptor.getValue();

        assertEquals(userToUpdate.getId(), updatedUser.getId());
        assertEquals(userToUpdate.getName(), updatedUser.getName());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());

        InOrder inOrder = Mockito.inOrder(userRepository);
        inOrder.verify(userRepository)
                .findById(userId);
        inOrder.verify(userRepository)
                .save(existingUser);
    }

    @Test
    void update_WhenFieldIsNull_ReturnExistingUserDto() {
        Long userId = 1L;
        User existingUser = User.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();
        UserDto userToUpdate = UserDto.builder()
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        userService.update(userId, userToUpdate);
        verify(userRepository).save(userArgumentCaptor.capture());

        User updatedUser = userArgumentCaptor.getValue();

        assertEquals(existingUser.getId(), updatedUser.getId());
        assertEquals(existingUser.getName(), updatedUser.getName());
        assertEquals(existingUser.getEmail(), updatedUser.getEmail());

        InOrder inOrder = Mockito.inOrder(userRepository);
        inOrder.verify(userRepository)
                .findById(userId);
        inOrder.verify(userRepository)
                .save(existingUser);
    }

    @Test
    void update_WhenUserNotFound_ThrowResourceNotFoundException() {
        Long userId = 666L;
        User existingUser = User.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();
        UserDto userToUpdate = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.update(userId, userToUpdate));

        verify(userRepository, never()).save(existingUser);
    }

    @Test
    void delete() {
        //TODO Реализовать проверку в DataJpaTest

        Long userId = 1L;
        userService.delete(userId);
        verify(userRepository, only()).deleteById(userId);
    }

    @Test
    void getById_WhenRightId_ReturnUserDto() {
        Long userId = 1L;
        User existingUser = User.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.toUserDto(existingUser)).thenReturn(userDto);

        UserDto userDtoOut = userService.getById(userId);

        assertEquals(userDto, userDtoOut);
        verify(userRepository, only()).findById(userId);
    }

    @Test
    void getById_WhenWrongId_ThrowResourceNotFoundException() {
        Long userId = 666L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(userId));

        verify(userRepository, only()).findById(userId);
    }

    @Test
    void getAll() {
        userService.getAll();
        verify(userRepository, only()).findAll();
    }
}