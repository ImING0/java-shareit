package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoIn {
    @NotBlank(message = "The name cannot be empty")
    private String name;
    @Email(message = "Invalid mail format")
    @NotBlank(message = "The email cannot be empty")
    private String email;
}
