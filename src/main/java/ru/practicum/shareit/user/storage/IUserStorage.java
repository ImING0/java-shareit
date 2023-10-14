package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserStorage {
    Optional<User> save(User user);

    Optional<User> update(Long userId, User user);

    void delete(Long userId);

    Optional<User> getUserById(Long userId);

    List<User> getAll();
}
