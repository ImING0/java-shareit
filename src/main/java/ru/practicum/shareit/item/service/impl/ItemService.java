package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.IItemService;
import ru.practicum.shareit.item.storage.IItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService implements IItemService {

    private final IItemStorage itemStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(Long userId,
                          Item item) {
        Item savedItem = itemStorage.save(userId, item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto update(Long userId,
                          Long itemId,
                          Item item) {
        return itemMapper.toItemDto(itemStorage.update(userId, itemId, item));
    }

    @Override
    public void delete(Long userId,
                       Long itemId) {
        itemStorage.delete(userId, itemId);
    }

    @Override
    public ItemDto getById(Long itemId) {
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
}
