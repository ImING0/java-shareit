package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceAlreadyExistsException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.IUserService;
import ru.practicum.shareit.user.storage.IUserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto create(User user) {
        throwIfEmailDuplicate(user);
        userStorage.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId,
                          User user) {
        throwIfUserNotFoundException(userId);
        return userMapper.toUserDto(userStorage.update(userId, user));
    }

    @Override
    public void delete(Long userId) {
        throwIfUserNotFoundException(userId);
        userStorage.delete(userId);
    }

    @Override
    public UserDto getById(Long userId) {
        throwIfUserNotFoundException(userId);
        return userStorage.findById(userId)
                .map(userMapper::toUserDto)
                .orElse(null);
    }

    @Override
    public List<UserDto> getAll() {
        return userMapper.toUserDtoList(userStorage.findAll());
    }

    private void throwIfUserNotFoundException(Long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id %d not found", userId)));
    }

    private void throwIfEmailDuplicate(User user) {
        if (userStorage.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException(String.format("User with email %s already exists",
                    user.getEmail()));
        }
    }
}
