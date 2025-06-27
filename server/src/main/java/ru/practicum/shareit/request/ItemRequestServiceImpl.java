package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponses;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper mapper;

    @Override
    @Transactional
    public ItemRequestDto createNewRequest(ItemRequestDto dto, long userId) {
        log.debug("Запрос на создание ItemRequest: user id = {}, description = {}", userId, dto.getDescription());
        User author = getUserOrElseThrow(userId);
        ItemRequest request = mapper.toItemRequest(dto, author);
        itemRequestRepository.save(request);
        log.info("Создан новый ItemRequest: user id = {}, description = {}", userId, dto.getDescription());
        return mapper.toItemRequestDto(request);
    }

    @Override
    public List<ItemRequestWithResponses> getUserRequests(long userId) {
        log.debug("Запрос на получение всех ItemRequest пользователя user id = {}", userId);
        User author = getUserOrElseThrow(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByAuthorOrderByCreatedDesc(author);
        List<ItemShortDto> itemShorts = itemRepository.findItemsByRequests(requests);

        Map<Long, List<ItemResponse>> mapRequestItemResponses = itemShorts.stream()
                .collect(Collectors.groupingBy(
                        ItemShortDto::getRequestId,
                        Collectors.mapping(mapper::toItemResponse, Collectors.toList())
                ));

        return requests.stream()
                .map(mapper::toItemRequestDto)
                .map(dto -> mapper.toItemRequestWithResponses(
                        dto,
                        mapRequestItemResponses.getOrDefault(dto.getId(), new ArrayList<>())))
                .toList();

    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(long userId) {
        log.debug("Запрос на получение ItemRequest других пользователей от user id = {}", userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByAuthor_IdNotOrderByCreatedDesc(userId);
        return mapper.toItemRequestDto(itemRequests);
    }

    @Override
    public ItemRequestWithResponses getRequest(long requestId) {
        log.debug("Запрос на получение ItemRequest request id = {}", requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest с id = " + requestId + " не существует"));

        ItemRequestDto itemRequestDto = mapper.toItemRequestDto(itemRequest);
        List<ItemShortDto> itemShorts = itemRepository.findItemsByRequest(itemRequest);
        List<ItemResponse> itemResponses = mapper.toItemResponse(itemShorts);
        return mapper.toItemRequestWithResponses(itemRequestDto, itemResponses);
    }

    private User getUserOrElseThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует"));
    }
}
