package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
public class UserDto {
    @Positive
    private final Long id;
    @NotBlank(message = "The name cannot be empty")
    private String name;
    @Email(message = "Invalid mail format")
    @NotBlank(message = "The email cannot be empty")
    private String email;
}
