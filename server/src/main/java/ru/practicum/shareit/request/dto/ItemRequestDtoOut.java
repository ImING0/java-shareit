package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDtoOut {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}
