package ru.practicum.shareit.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class ImMemoryIdGenerator implements IdGenerator {

    private Long id = 0L;

    @Override
    public Long generateId() {
        id++;
        return id;
    }
}
