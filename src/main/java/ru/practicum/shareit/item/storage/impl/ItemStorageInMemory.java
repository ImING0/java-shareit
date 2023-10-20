package ru.practicum.shareit.item.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.IllegalOwnerException;
import ru.practicum.shareit.exception.IllegalNameOrDescriptionException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.IItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.impl.UserStorageInMemory;
import ru.practicum.shareit.util.IdGenerator;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ItemStorageInMemory implements IItemStorage {

    private final IdGenerator idGenerator;
    private final UserStorageInMemory userStorageInMemory;
    private final HashMap<Long, Item> items; // itemId -> item
    private final HashMap<Long, Set<Long>> userItems; // userId -> itemIds
    //TODO add feadback storage

    @Override
    public Optional<Item> save(Long userId,
                               Item item) {
        User user = userStorageInMemory.findById(userId)
                .get();
        Long requestId = item.getRequest();

        Item itemToSave = Item.builder()
                .id(idGenerator.generateId())
                .owner(user.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(true)
                .request(requestId)
                .build();
        items.put(itemToSave.getId(), itemToSave);
        if (userItems.containsKey(userId)) {
            userItems.get(userId)
                    .add(itemToSave.getId());
        } else {
            Set<Long> userItemIds = Set.of(itemToSave.getId());
            userItems.put(userId, userItemIds);
        }
        return Optional.of(itemToSave);
    }

    @Override
    public Optional<Item> update(Long userId,
                                 Long itemId,
                                 Item item) {
        throwIfItemNotFound(itemId);
        throwIfNotOwner(userId, itemId);
        throwIfAllFieldsAreNull(item);
        Item itemToUpdate = items.get(itemId);
        if (item.getName() != null && !item.getName()
                .isBlank()) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription()
                .isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        items.put(itemId, itemToUpdate);
        return Optional.of(itemToUpdate);
    }

    @Override
    public void delete(Long userId,
                       Long itemId) {
        throwIfItemNotFound(itemId);
        throwIfNotOwner(userId, itemId);
        items.remove(itemId);
        userItems.remove(userId, itemId);
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        throwIfItemNotFound(itemId);
        return Optional.of(items.get(itemId));
    }

    @Override
    public List<Item> findAllOwnerItemsByOwnerId(Long ownerId) {
        User user = userStorageInMemory.findById(ownerId)
                .get();
        List<Item> itemsOfUser = new ArrayList<>();
        if (!userItems.containsKey(ownerId)) {
            return List.of();
        } else {
            Set<Long> itemIds = userItems.get(user.getId());
            for (Long id : itemIds) {
                itemsOfUser.add(items.get(id));
            }
        }
        return itemsOfUser;
    }

    @Override
    public List<Item> search(String name) {
        List<Item> itemsToReturn = new ArrayList<>();
        name = name.toLowerCase();
        for (Item item : items.values()) {
            if (item.getName()
                    .toLowerCase()
                    .contains(name) || item.getDescription()
                    .toLowerCase()
                    .contains(name) && item.getAvailable()) {
                itemsToReturn.add(item);
            }
        }
        return itemsToReturn;
    }

    private void throwIfNotOwner(Long userId,
                                 Long itemId) {
        if (!items.get(itemId)
                .getOwner()
                .equals(userId)) {
            throw new IllegalOwnerException("Item owner is not the same as user");
        }
    }

    private void throwIfNameOrDescriptionIsNull(Item item) {
        if (item.getName() == null || item.getDescription() == null) {
            throw new IllegalNameOrDescriptionException(
                    "Item name and description must be not null");
        }
    }

    private void throwIfItemNotFound(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ResourceNotFoundException(String.format("Item with id %d not found", itemId));
        }
    }

    private void throwIfItemAvailableIsNull(Item item) {
        if (item.getAvailable() == null) {
            throw new NotAvailableException("Item availability must be not null");
        }
    }

    private void throwIfAllFieldsAreNull(Item item) {
        if (item.getName() == null && item.getDescription() == null && item.getAvailable() == null
                && item.getRequest() == null) {
            throw new IllegalNameOrDescriptionException(
                    "Item name, description, availability and request must be not null");
        }
    }
}
