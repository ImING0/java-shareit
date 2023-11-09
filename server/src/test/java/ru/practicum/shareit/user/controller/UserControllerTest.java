package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.impl.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void createUser_WhenValidData_ReturnUserDto() {
        UserDto userToCreate = UserDto.builder()
                .name("AlexPositive")
                .email("AlexPositive@mail.ru")
                .build();

        when(userService.create(userToCreate)).thenReturn(userToCreate);

        String result = mockMvc.perform(post("/users").contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userToCreate), result);
    }

    @Test
    @SneakyThrows
    void updateUser_WhenValidData_ReturnUpdatedUserDto() {
        Long userId = 1L;
        UserDto userToUpdate = UserDto.builder()
                .name("AlexPositiveNew")
                .email("AlexPositiveNew@mail.ru")
                .build();
        when(userService.update(userId, userToUpdate)).thenReturn(userToUpdate);

        String result = mockMvc.perform(
                        patch("/users/{userId}", userId).contentType("application/json")
                                .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userToUpdate), result);
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        Long userId = 1L;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(userId);
    }

    @Test
    @SneakyThrows
    void getUserById() {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("AlexPositive")
                .email("AlexPositive@gmail.com")
                .build();
        when(userService.getById(userId)).thenReturn(userDto);

        String result = mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @Test
    @SneakyThrows
    void getAllUsers() {
        List<UserDto> userDtoOut = List.of(
                new UserDto(1L, "AlexPositive", "AlexPositive@gmail.com"),
                new UserDto(2L, "JohnDoe", "JohnDoe@gmail.com"),
                new UserDto(3L, "JaneSmith", "JaneSmith@gmail.com"));
        when(userService.getAll()).thenReturn(userDtoOut);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userDtoOut), result);
    }
}