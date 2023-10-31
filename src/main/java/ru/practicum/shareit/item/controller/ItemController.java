package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.IItemService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
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
        return ResponseEntity.ok(itemService.create(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(requestHeader) Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        log.info("updateItem request: userId = {}, itemId = {}, item = {}", userId, itemId, item);
        return ResponseEntity.ok(itemService.update(userId, itemId, item));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId,
                                               @RequestHeader(requestHeader) Long userId) {
        log.info("getItemById request: itemId = {}", itemId);
        return ResponseEntity.ok(itemService.getById(itemId, userId));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDtoOut> addComment(@PathVariable Long itemId,
                                                    @RequestHeader(requestHeader) Long userId,
                                                    @RequestBody @Valid CommentDtoIn commentDtoIn) {
        log.info("addComment request: itemId = {}, userId = {}, commentDtoIn = {}", itemId, userId,
                commentDtoIn);
        return ResponseEntity.ok(itemService.addComment(itemId, userId, commentDtoIn));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllOwnerItemsByOwnerId(
            @RequestHeader(requestHeader) Long ownerId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("getAllOwnerItemsByOwnerId request: ownerId = {}", ownerId);
        return ResponseEntity.ok(itemService.getAllOwnerItemsByOwnerId(ownerId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam("text") String text,
                                                    @RequestParam(name = "from", defaultValue = "0")
                                                    @PositiveOrZero Integer from,
                                                    @RequestParam(name = "size",
                                                            defaultValue = "10")
                                                    @PositiveOrZero Integer size) {
        log.info("searchItem request: text = {}", text);
        return ResponseEntity.ok(itemService.search(text, from, size));
    }
}

