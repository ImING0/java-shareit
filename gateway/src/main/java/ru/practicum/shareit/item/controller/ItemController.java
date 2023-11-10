package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;


import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final String requestHeader = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<ItemDtoOut> createItem(@RequestHeader(requestHeader) Long userId,
                                                 @RequestBody @Valid ItemDtoIn itemDtoIn) {
        log.info("createItem request: userId = {}, item = {}", userId, itemDtoIn);
        return ResponseEntity.ok(itemClient.create(userId, itemDtoIn).getBody());
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDtoOut> updateItem(@RequestHeader(requestHeader) Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody ItemDtoIn itemDtoIn) {
        log.info("updateItem request: userId = {}, itemId = {}, item = {}", userId, itemId,
                itemDtoIn);
        return ResponseEntity.ok(itemClient.update(userId, itemId, itemDtoIn).getBody());
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoOut> getItemById(@PathVariable Long itemId,
                                                 @RequestHeader(requestHeader) Long userId) {
        log.info("getItemById request: itemId = {}", itemId);
        return ResponseEntity.ok(itemClient.getById(itemId, userId).getBody());
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDtoOut> addComment(@PathVariable Long itemId,
                                                    @RequestHeader(requestHeader) Long userId,
                                                    @RequestBody @Valid CommentDtoIn commentDtoIn) {
        log.info("addComment request: itemId = {}, userId = {}, commentDtoIn = {}", itemId, userId,
                commentDtoIn);
        return ResponseEntity.ok(itemClient.addComment(itemId, userId, commentDtoIn).getBody());
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoOut>> getAllOwnerItemsByOwnerId(
            @RequestHeader(requestHeader) Long ownerId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("getAllOwnerItemsByOwnerId request: ownerId = {}", ownerId);
        return ResponseEntity.ok(itemClient.getAllOwnerItemsByOwnerId(ownerId, from, size).getBody());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDtoOut>> searchItem(@RequestParam("text") String text,
                                                      @RequestParam(name = "from", defaultValue = "0")
                                                    @PositiveOrZero Integer from,
                                                      @RequestParam(name = "size",
                                                            defaultValue = "10")
                                                    @PositiveOrZero Integer size) {
        log.info("searchItem request: text = {}", text);
        return ResponseEntity.ok(itemClient.search(text, from, size).getBody());
    }
}

