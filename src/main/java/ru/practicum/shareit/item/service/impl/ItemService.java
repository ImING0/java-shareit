package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.IllegalOwnerException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.IItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService implements IItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(Long userId,
                          ItemDto itemDto) {
        throwIfUserNotFound(userId);
        itemDto.setOwner(userId);
        return itemMapper.toItemDto(itemRepository.save(itemMapper.toItem(itemDto)));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId,
                          Long itemId,
                          ItemDto itemDto) {
        throwIfUserNotFound(userId);
        throwIfAllFieldsAreNull(itemDto);
        throwIfNotOwner(userId, itemId);
        Item existingItem = getItemOrThrow(itemId);
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId,
                           Long userId) {
        Item item = getItemOrThrow(itemId);
        ItemDto itemDto = itemMapper.toItemDto(item);
        setBookings(itemDto, userId);
        setComments(itemDto);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllOwnerItemsByOwnerId(Long ownerId,
                                                   Integer from,
                                                   Integer size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size);
        return itemRepository.findAllByOwner(ownerId, pageable)
                .stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.toItemDto(item);
                    setBookings(itemDto, ownerId);
                    setComments(itemDto);
                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String name,
                                Integer from,
                                Integer size) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        Pageable pageable = new OffsetBasedPageRequest(from, size);
        return itemRepository.search(name, pageable)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDtoOut addComment(Long itemId,
                                    Long userId,
                                    CommentDtoIn commentDtoIn) {
        User user = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);
        /*Сразу взяли все прошедшие брони*/
        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBefore(userId,
                        itemId, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("You can't comment on this item"));
        return commentMapper.toCommentDtoOut(commentRepository.save(Comment.builder()
                .text(commentDtoIn.getText())
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build()));
    }

    private void setBookings(ItemDto itemDto,
                             Long userId) {
        if (itemDto.getOwner()
                .equals(userId)) {
            BookingDtoOut lastBooking
                    = bookingRepository.findFirstByItemOwnerAndItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
                            userId, itemDto.getId(), LocalDateTime.now(), Status.APPROVED)
                    .map(bookingMapper::toBookingDtoOut)
                    .orElse(null);
            BookingDtoOut nextBooking
                    = bookingRepository.findFirstByItemOwnerAndItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                            userId, itemDto.getId(), LocalDateTime.now(), Status.APPROVED)
                    .map(bookingMapper::toBookingDtoOut)
                    .orElse(null);
            itemDto.setLastBooking(lastBooking);
            itemDto.setNextBooking(nextBooking);
        }
    }

    private void setComments(ItemDto itemDto) {
        List<CommentDtoOut> comments = commentRepository.findAllByItemId(itemDto.getId())
                .stream()
                .map(commentMapper::toCommentDtoOut)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id %d not found", userId)));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Item with id %d not found", itemId)));
    }

    private void throwIfUserNotFound(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(String.format("User with id %d not found", userId));
        }
    }

    private void throwIfItemNotFound(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException(String.format("Item with id %d not found", itemId));
        }
    }

    private void throwIfAllFieldsAreNull(ItemDto itemDto) {
        if (itemDto.getName() == null && itemDto.getDescription() == null
                && itemDto.getAvailable() == null && itemDto.getRequestId() == null) {
            throw new BadRequestException(
                    "Item name, description, availability and request must be not null");
        }
    }

    private void throwIfNotOwner(Long userId, Long itemId) {
        if (!itemRepository.existsByIdAndOwner(itemId, userId)) {
            throw new IllegalOwnerException(String.format("User with id %d is not owner of item with id %d",
                    userId, itemId));
        }
    }
}
