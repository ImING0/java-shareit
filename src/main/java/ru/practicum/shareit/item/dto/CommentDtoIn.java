package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommentDtoIn {
    @NotBlank(message = "Text is mandatory")
    @NotNull(message = "Text is mandatory")
    private String text;
}
