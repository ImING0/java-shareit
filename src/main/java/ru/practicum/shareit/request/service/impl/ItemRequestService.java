package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.IItemRequestService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService implements IItemRequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ItemRequestDtoOut create(ItemRequestDtoIn itemRequestDtoIn,
                                    Long userId) {
        throwIfUserNotFound(userId);
        return requestMapper.toDtoOut(
                itemRequestRepository.save(requestMapper.fromDtoIn(itemRequestDtoIn)));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDtoOut getById(Long id,
                                     Long userId) {
        throwIfUserNotFound(userId);
        return itemRequestRepository.findById(id)
                .map(requestMapper::toDtoOut)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Request with id %d not found", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoOut> getAllByUserId(Long userId) {
        throwIfUserNotFound(userId);
        return itemRequestRepository.findAllByRequestorOrderByCreatedDesc(userId)
                .stream()
                .map(requestMapper::toDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoOut> getAllFromOthers(Long userId,
                                                    Integer from,
                                                    Integer size) {
        throwIfUserNotFound(userId);
        Pageable pageable = new OffsetBasedPageRequest(from, size,
                Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestRepository.findAllByRequestorNot(userId, pageable)
                .stream()
                .map(requestMapper::toDtoOut)
                .collect(Collectors.toList());
    }

    private void throwIfUserNotFound(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(String.format("User with id %d not found", userId));
        }
    }
}
