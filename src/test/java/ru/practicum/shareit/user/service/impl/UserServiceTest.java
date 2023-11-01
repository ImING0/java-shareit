package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    /*@Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    private final UserDto userDto = UserDto.builder().id(1L)
            .name("Alex")
            .email("firstUser@gmail.com")
            .build();

    @Test
    void createUserWithValidDataAndReturnDto() {
        // допиши тест, который проверяет создание пользователя
        User userToSave = User.builder().id(1L)
                .name("Alex")
                .email("firstUser@gmail.com")
                .build();
        when(userRepository.save(userToSave)).thenReturn(userToSave);
        when(userMapper.toUserDto(userToSave)).thenReturn(userDto);

        UserDto userDtoOut = userService.create(userToSave);
        assertEquals(userDto, userDtoOut);
        verify(userRepository).save(userToSave);
    }

    @Test
    void update_WhenDataValid_ReturnUpdatedUserDto() {
        Long userId = 1L;
        User existingUser = new User();
        User userForUpdate = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(userForUpdate);
        when(userMapper.toUserDto(existingUser)).thenReturn(userMapper.toUserDto(userForUpdate));

        UserDto userDtoOut = userService.update(userId, userForUpdate);

    }*/

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