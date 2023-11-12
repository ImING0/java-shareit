package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequest fromDtoIn(ItemRequestDtoIn itemRequestDtoIn) {
        return ItemRequest.builder()
                .description(itemRequestDtoIn.getDescription())
                .requestor(itemRequestDtoIn.getRequestorId())
                .created(LocalDateTime.now())
                .build();
    }

    public ItemRequestDtoOut toDtoOut(ItemRequest itemRequest) {
        return ItemRequestDtoOut.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() == null ? null : itemRequest.getItems()
                        .stream()
                        .map(itemMapper::toItemDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
