package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.IUserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto update(Long userId,
                          UserDto userDto) {
        throwIfUserNotFoundException(userId);
        User existingUser = userRepository.findById(userId)
                .get();
        if (userDto.getName() != null && !userDto.getName()
                .isBlank()) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail()
                .isBlank()) {
            existingUser.setEmail(userDto.getEmail());
        }

        return userMapper.toUserDto(userRepository.save(existingUser));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long userId) {
        return userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id %d not found", userId))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void throwIfUserNotFoundException(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(String.format("User with id %d not found", userId));
        }
    }
}
