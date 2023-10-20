package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IllegalNameOrDescriptionException;
import ru.practicum.shareit.exception.IllegalOwnerException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.IItemService;
import ru.practicum.shareit.item.storage.IItemStorage;
import ru.practicum.shareit.user.storage.IUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService implements IItemService {

    private final IItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final IUserStorage userStorage;

    @Override
    public ItemDto create(Long userId,
                          Item item) {
        throwIfUserNotFound(userId);
        Item savedItem = itemStorage.save(userId, item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto update(Long userId,
                          Long itemId,
                          Item item) {
        throwIfItemNotFound(itemId);
        throwIfNotOwner(userId, itemId);
        throwIfAllFieldsAreNull(item);
        return itemMapper.toItemDto(itemStorage.update(userId, itemId, item));
    }

    @Override
    public void delete(Long userId,
                       Long itemId) {
        throwIfItemNotFound(itemId);
        throwIfNotOwner(userId, itemId);
        itemStorage.delete(userId, itemId);
    }

    @Override
    public ItemDto getById(Long itemId) {
        throwIfItemNotFound(itemId);
        return itemStorage.findById(itemId)
                .map(itemMapper::toItemDto)
                .get();
    }

    @Override
    public List<ItemDto> getAllOwnerItemsByOwnerId(Long ownerId) {
        return itemStorage.findAllOwnerItemsByOwnerId(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            return List.of();
        }
        return itemStorage.search(name)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void throwIfUserNotFound(Long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }

    private void throwIfItemNotFound(Long itemId) {
        itemStorage.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id " + itemId + " not found"));
    }
    private void throwIfAllFieldsAreNull(Item item) {
        if (item.getName() == null && item.getDescription() == null && item.getAvailable() == null
                && item.getRequest() == null) {
            throw new IllegalNameOrDescriptionException(
                    "Item name, description, availability and request must be not null");
        }
    }

    private void throwIfNotOwner(Long userId,
                                 Long itemId) {
        if (!itemStorage.findById(itemId)
                .get()
                .getOwner()
                .equals(userId)) {
            throw new IllegalOwnerException("Item owner is not the same as user");
        }
    }
}
