package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
public class ItemController {

    private final String REQUEST_HEADER = "X-Sharer-User-Id";
    private final IItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(REQUEST_HEADER) Long userId,
            @RequestBody @Valid Item item) {
        return ResponseEntity.ok(itemService.createItem(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(REQUEST_HEADER) Long userId,
            @PathVariable Long itemId, @RequestBody Item item) {
        return ResponseEntity.ok(itemService.updateItem(userId, itemId, item));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader(REQUEST_HEADER) Long userId,
            @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemById(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllOwnerItemsByOwnerId(
            @RequestHeader(REQUEST_HEADER) Long ownerId) {
        return ResponseEntity.ok(itemService.getAllOwnerItemsByOwnerId(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam("text") String text) {
        return ResponseEntity.ok(itemService.searchItem(text));
    }
}

