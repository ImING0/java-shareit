package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface IItemStorage {

    Optional<Item> save(Long userId,
                        Item item);

    Optional<Item> update(Long userId,
                          Long itemId,
                          Item item);

    void delete(Long userId,
                Long itemId);

    Optional<Item> findById(Long itemId);

    List<Item> findAllOwnerItemsByOwnerId(Long ownerId);

    List<Item> search(String name);
}
