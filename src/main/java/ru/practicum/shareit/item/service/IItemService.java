package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface IItemService {

    ItemDto create(Long userId,
                   ItemDto itemDto);

    ItemDto update(Long userId,
                   Long itemId,
                   ItemDto itemDto);

    ItemDto getById(Long itemId,
                    Long userId);

    List<ItemDto> getAllOwnerItemsByOwnerId(Long ownerId,
                                            Integer from,
                                            Integer size);

    List<ItemDto> search(String name,
                         Integer from,
                         Integer size);

    CommentDtoOut addComment(Long itemId,
                             Long userId,
                             CommentDtoIn commentDtoIn);
}
