package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface IUserService {
    UserDto create(User user);

    UserDto update(Long userId,
                   User user);

    void delete(Long userId);

    UserDto getById(Long userId);

    List<UserDto> getAll();
}
