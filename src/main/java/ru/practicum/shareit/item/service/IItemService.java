package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface IItemService {

    ItemDto createItem(Long userId, Item item);

    ItemDto updateItem(Long userId, Long itemId, Item item);

    void deleteItem(Long userId, Long itemId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllOwnerItemsByOwnerId(Long ownerId);

    List<ItemDto> searchItem(String name);
}
