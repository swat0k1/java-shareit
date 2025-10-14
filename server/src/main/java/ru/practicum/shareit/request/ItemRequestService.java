package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemStorageDb;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserStorageDb;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class ItemRequestService {

    private final ItemRequestStorageDb itemRequestStorageDb;
    private final UserStorageDb userStorageDb;
    private final ItemStorageDb itemStorageDb;

    public ItemRequestService(ItemRequestStorageDb itemRequestStorageDb, UserStorageDb userStorageDb, ItemStorageDb itemStorageDb) {
        this.itemRequestStorageDb = itemRequestStorageDb;
        this.userStorageDb = userStorageDb;
        this.itemStorageDb = itemStorageDb;
    }

    @Transactional
    public ItemRequestDto createItemRequest(int requestorId, ItemRequestCreateDto itemRequestCreateDto) {
        User requestor = getUserById(requestorId);
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestCreateDto, requestor);
        return ItemRequestMapper.mapToItemRequestDto(itemRequestStorageDb.save(itemRequest));
    }

    public List<ItemRequestItemsDto> findUserItemRequests(int requestorId) {
        User requestor = getUserById(requestorId);
        List<ItemRequest> requests = itemRequestStorageDb.findAllByRequestorIdOrderByCreatedDesc(requestorId);
        List<Item> items = itemStorageDb.findAllByItemRequestIdIn(getRequestIds(requests));
        return ItemRequestMapper.mapToItemRequestDtos(requests, items);
    }

    public List<ItemRequestDto> findRequestsOfAnotherUser(int userId) {
        getUserById(userId);
        List<ItemRequest> requests = itemRequestStorageDb.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        return ItemRequestMapper.mapToItemRequestDtos(requests);
    }

    public ItemRequestItemsDto findItemRequestById(int requestId) {
        ItemRequest itemRequest = getItemRequestById(requestId);
        List<Item> items = itemStorageDb.findAllByItemRequestId(requestId);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest, items);
    }

    private User getUserById(int userId) {
        return userStorageDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private ItemRequest getItemRequestById(int requestId) {
        return itemRequestStorageDb.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request not found: " + requestId));
    }

    private List<Integer> getRequestIds(List<ItemRequest> requests) {
        return requests.stream().map(ItemRequest::getId).toList();
    }

}
