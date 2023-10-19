package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
public class UserDto {
    private final Long id;
    private String name;
    private String email;
}
