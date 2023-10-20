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
    public ItemDto createItem(Long userId,
                              Item item) {
        Item savedItem = itemStorage.save(userId, item)
                .get();
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId,
                              Long itemId,
                              Item item) {
        return itemStorage.update(userId, itemId, item)
                .map(itemMapper::toItemDto)
                .get();
    }

    @Override
    public void deleteItem(Long userId,
                           Long itemId) {
        itemStorage.delete(userId, itemId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemStorage.getItemById(itemId)
                .map(itemMapper::toItemDto)
                .get();
    }

    @Override
    public List<ItemDto> getAllOwnerItemsByOwnerId(Long ownerId) {
        return itemStorage.getAllOwnerItemsByOwnerId(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            return List.of();
        }
        return itemStorage.searchItem(name)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
