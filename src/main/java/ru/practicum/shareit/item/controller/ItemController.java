package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.IItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final String requestHeader = "X-Sharer-User-Id";
    private final IItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(requestHeader) Long userId,
                                              @RequestBody @Valid ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        log.info("createItem request: userId = {}, item = {}", userId, item);
        return ResponseEntity.ok(itemService.createItem(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(requestHeader) Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        log.info("updateItem request: userId = {}, itemId = {}, item = {}", userId, itemId, item);
        return ResponseEntity.ok(itemService.updateItem(userId, itemId, item));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader(requestHeader) Long userId,
                                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
        log.info("deleteItem request: userId = {}, itemId = {}", userId, itemId);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        log.info("getItemById request: itemId = {}", itemId);
        return ResponseEntity.ok(itemService.getItemById(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllOwnerItemsByOwnerId(
            @RequestHeader(requestHeader) Long ownerId) {
        log.info("getAllOwnerItemsByOwnerId request: ownerId = {}", ownerId);
        return ResponseEntity.ok(itemService.getAllOwnerItemsByOwnerId(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam("text") String text) {
        log.info("searchItem request: text = {}", text);
        return ResponseEntity.ok(itemService.searchItem(text));
    }
}

