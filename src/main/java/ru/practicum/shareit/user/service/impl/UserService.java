package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        userStorage.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId,
                          User user) {
        return userStorage.update(userId, user)
                .map(userMapper::toUserDto)
                .orElse(null);
    }

    @Override
    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    @Override
    public UserDto getById(Long userId) {
        return userStorage.getById(userId)
                .map(userMapper::toUserDto)
                .orElse(null);
    }

    @Override
    public List<UserDto> getAll() {
        return userMapper.toUserDtoList(userStorage.getAll());
    }
}
