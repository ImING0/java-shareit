package ru.practicum.shareit.user.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ResourceAlreadyExistsException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
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
    public Optional<User> save(User user) {
        throwIfEmailDuplicate(user);
        user.setId(idGenerator.generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(Long userId,
                                 User user) {
        throwIfUserNotFoundException(userId);
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
        return Optional.of(existingUser);
    }

    @Override
    public void delete(Long userId) {
        throwIfUserNotFoundException(userId);
        User user = users.get(userId);
        emails.remove(user.getEmail());
        users.remove(userId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        throwIfUserNotFoundException(userId);
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    private void throwIfEmailDuplicate(User user) {
        if (emails.contains(user.getEmail())) {
            throw new ResourceAlreadyExistsException("User with this email already exists");
        }
    }

    private void throwIfUserNotFoundException(Long userId) {
        if (!users.containsKey(userId)) {
            throw new ResourceNotFoundException(String.format("User with id %d not found", userId));
        }
    }
}
