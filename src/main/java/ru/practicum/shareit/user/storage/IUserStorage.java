package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserStorage {
    User save(User user);

    User update(Long userId,
                User user);

    void delete(Long userId);

    Optional<User> findById(Long userId);

    List<User> findAll();
}
