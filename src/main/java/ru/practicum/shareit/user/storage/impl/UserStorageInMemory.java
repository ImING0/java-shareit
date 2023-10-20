package ru.practicum.shareit.user.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ResourceAlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.IUserStorage;
import ru.practicum.shareit.util.IdGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserStorageInMemory implements IUserStorage {

    private final HashMap<Long, User> users;
    private final HashSet<String> emails;
    private final IdGenerator idGenerator;

    @Override
    public User save(User user) {
        user.setId(idGenerator.generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(Long userId,
                       User user) {
        User existingUser = findById(userId).get();
        if (emails.contains(user.getEmail()) && !user.getEmail()
                .equals(existingUser.getEmail())) {
            throw new ResourceAlreadyExistsException("User with this email already exists");
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            emails.remove(existingUser.getEmail());
            existingUser.setEmail(user.getEmail());
            emails.add(user.getEmail());
        }

        users.put(userId, existingUser);
        return existingUser;
    }

    @Override
    public void delete(Long userId) {
        User user = users.get(userId);
        emails.remove(user.getEmail());
        users.remove(userId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    public boolean existsByEmail(String email) {
        return emails.contains(email);
    }
}
