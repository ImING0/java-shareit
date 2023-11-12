package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface IUserService {
    UserDto create(UserDto userDto);

    UserDto update(Long userId,
                   UserDto userDto);

    void delete(Long userId);

    UserDto getById(Long userId);

    List<UserDto> getAll();
}
