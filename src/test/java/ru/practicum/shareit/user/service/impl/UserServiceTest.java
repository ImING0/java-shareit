package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
                .email("alexPositive@gmail.com").build();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        userService.update(userId, userToUpdate);
        verify(userRepository).save(userArgumentCaptor.capture());
        User updatedUser = userArgumentCaptor.getValue();

        assertEquals(userToUpdate.getId(), updatedUser.getId());
        assertEquals(userToUpdate.getName(), updatedUser.getName());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());
    }

    @Test
    void delete() {
    }

    @Test
    void getById() {
    }

    @Test
    void getAll() {
    }
}