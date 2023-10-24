package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.IItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService implements IItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId,
                          Item item) {
        throwIfUserNotFound(userId);
        item.setOwner(userId);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId,
                          Long itemId,
                          Item item) {
        throwIfUserNotFound(userId);
        throwIfItemNotFound(itemId);
        throwIfAllFieldsAreNull(item);

        Item existingItem = itemRepository.findById(itemId)
                .get();
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public ItemDto getById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Item with id %d not found", itemId)));
    }

    @Override
    public List<ItemDto> getAllOwnerItemsByOwnerId(Long ownerId) {
        return itemRepository.findAllByOwner(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return itemRepository.search(name)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void throwIfUserNotFound(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id %d not found", userId)));
    }

    private void throwIfItemNotFound(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Item with id %d not found", itemId)));
    }

    private void throwIfAllFieldsAreNull(Item item) {
        if (item.getName() == null && item.getDescription() == null && item.getAvailable() == null
                && item.getRequest() == null) {
            throw new BadRequestException(
                    "Item name, description, availability and request must be not null");
        }
    }

    /*
    private void throwIfNotOwner(Long userId,
                                 Long itemId) {
        if (!itemStorage.findById(itemId)
                .get()
                .getOwner()
                .equals(userId)) {
            throw new IllegalOwnerException("Item owner is not the same as user");
        }
    }*/
}
