package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {
    @Positive
    private  Long id;
    @NotBlank(message = "The name cannot be empty")
    private String name;
    @Email (message = "Invalid mail format")
    @NotBlank(message = "The email cannot be empty")
    private String email;
}
