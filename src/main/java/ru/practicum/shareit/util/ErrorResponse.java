package ru.practicum.shareit.util;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private String error;
    private String message;
    private int code;
    private List<String> fieldErrors;
}
