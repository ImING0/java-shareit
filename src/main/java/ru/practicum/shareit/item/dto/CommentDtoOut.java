package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDtoOut {
    private Long id;
    private String text;
    private Long itemId;
    private Long authorId;
    private String authorName;
    private String created;
}
