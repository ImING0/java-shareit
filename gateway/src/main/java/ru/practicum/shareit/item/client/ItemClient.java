package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}")String baseUrl) {
        super(baseUrl + API_PREFIX);
    }

    public ResponseEntity<ItemDtoOut> create(Long userId,
                                             ItemDtoIn itemDtoIn) {
        return post("", itemDtoIn, userId, ItemDtoOut.class);
    }

    public ResponseEntity<ItemDtoOut> update(Long userId,
                                             Long itemId,
                                             ItemDtoIn itemDtoIn) {
        return patch("/" + itemId.toString(), itemDtoIn, userId, ItemDtoOut.class);
    }

    public ResponseEntity<ItemDtoOut> getById(Long itemId,
                                              Long userId) {
        return get("/" + itemId.toString(), userId, Map.of(), ItemDtoOut.class);
    }

    public ResponseEntity<CommentDtoOut> addComment(Long itemId,
                                                    Long userId,
                                                    CommentDtoIn commentDtoIn) {
        return post("/" + itemId.toString() + "/comment", commentDtoIn, userId,
                CommentDtoOut.class);
    }

    public ResponseEntity<List<ItemDtoOut>> getAllOwnerItemsByOwnerId(Long ownerId,
                                                                      Integer from,
                                                                      Integer size) {
        return getAll("", ownerId, Map.of("from", from.toString(), "size", size.toString()),
                ItemDtoOut[].class);
    }

    public ResponseEntity<List<ItemDtoOut>> search(String text,
                                                   Integer from,
                                                   Integer size) {
        return getAll("/search", Map.of("text", text, "from", from.toString(), "size",
                size.toString()), ItemDtoOut[].class);
    }
}
