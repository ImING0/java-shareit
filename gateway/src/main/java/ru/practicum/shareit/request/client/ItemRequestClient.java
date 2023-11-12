package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String baseUrl) {
        super(baseUrl + API_PREFIX);
    }

    public ResponseEntity<ItemRequestDtoOut> create(ItemRequestDtoIn itemRequestDtoIn,
                                                    Long userId) {
        return post("", itemRequestDtoIn, userId, ItemRequestDtoOut.class);
    }

    public ResponseEntity<ItemRequestDtoOut> getById(Long requestId,
                                                     Long userId) {
        return get("/" + requestId.toString(), userId, Map.of(), ItemRequestDtoOut.class);
    }

    public ResponseEntity<List<ItemRequestDtoOut>> getAllByUserId(Long userId) {
        return getAll("", userId, Map.of(), ItemRequestDtoOut[].class);
    }

    public ResponseEntity<List<ItemRequestDtoOut>> getAllFromOthers(Long userId,
                                                                    Integer from,
                                                                    Integer size) {
        return getAll("/all", userId, Map.of("from", from.toString(), "size", size.toString()),
                ItemRequestDtoOut[].class);
    }
}
