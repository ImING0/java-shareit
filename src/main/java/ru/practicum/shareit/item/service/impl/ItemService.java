package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.IItemService;
import ru.practicum.shareit.user.repository.UserRepository;

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

    @Override
    public ItemDto create(Long userId,
                          Item item) {
        throwIfUserNotFound(userId);
        item.setOwner(userId);
        return itemMapper.toItemDtoWithoutBooking(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId,
                          Long itemId,
                          Item item) {
        throwIfUserNotFound(userId);
        throwIfItemNotFound(itemId);
        throwIfAllFieldsAreNull(item);

        Item existingItem = itemRepository.findById(itemId)
                .get();
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        return itemMapper.toItemDtoWithoutBooking(itemRepository.save(existingItem));
    }

    @Override
    public ItemDto getById(Long itemId,
                           Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Item with id %d not found", itemId)));
        if (item.getOwner()
                .equals(userId)) {
            BookingDtoOut lastBooking
                    = bookingRepository.findFirstByItemOwnerAndItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
                            userId, itemId, LocalDateTime.now(), Status.APPROVED)
                    .map(bookingMapper::toBookingDtoOut)
                    .orElse(null);

            BookingDtoOut nextBooking
                    = bookingRepository.findFirstByItemOwnerAndItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                            userId, itemId, LocalDateTime.now(), Status.APPROVED)
                    .map(bookingMapper::toBookingDtoOut)
                    .orElse(null);
            return itemMapper.toItemDtoWithBooking(item, lastBooking, nextBooking);
        } else {
            return itemMapper.toItemDtoWithoutBooking(item);
        }
    }

    @Override
    public List<ItemDto> getAllOwnerItemsByOwnerId(Long ownerId) {
        return itemRepository.findAllByOwner(ownerId)
                .stream()
                .map(item -> {
                    BookingDtoOut lastBooking
                            = bookingRepository.findFirstByItemOwnerAndItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
                                    item.getOwner(), item.getId(), LocalDateTime.now(), Status.APPROVED)
                            .map(bookingMapper::toBookingDtoOut)
                            .orElse(null);
                    BookingDtoOut nextBooking
                            = bookingRepository.findFirstByItemOwnerAndItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                                    item.getOwner(), item.getId(), LocalDateTime.now(), Status.APPROVED)
                            .map(bookingMapper::toBookingDtoOut)
                            .orElse(null);
                    return itemMapper.toItemDtoWithBooking(item, lastBooking, nextBooking);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return itemRepository.search(name)
                .stream()
                .map(itemMapper::toItemDtoWithoutBooking)
                .collect(Collectors.toList());
    }

    private void throwIfUserNotFound(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id %d not found", userId)));
    }

    private void throwIfItemNotFound(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Item with id %d not found", itemId)));
    }

    private void throwIfAllFieldsAreNull(Item item) {
        if (item.getName() == null && item.getDescription() == null && item.getAvailable() == null
                && item.getRequest() == null) {
            throw new BadRequestException(
                    "Item name, description, availability and request must be not null");
        }
    }

    /*
    private void throwIfNotOwner(Long userId,
                                 Long itemId) {
        if (!itemStorage.findById(itemId)
                .get()
                .getOwner()
                .equals(userId)) {
            throw new IllegalOwnerException("Item owner is not the same as user");
        }
    }*/
}
