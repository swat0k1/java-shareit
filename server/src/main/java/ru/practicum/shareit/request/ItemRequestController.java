package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(REQUEST_HEADER) int requestorId,
                                            @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestService.createItemRequest(requestorId, itemRequestCreateDto);
    }

    @GetMapping
    public List<ItemRequestItemsDto> getUserRequests(@RequestHeader(REQUEST_HEADER) int requestorId) {
        return itemRequestService.findUserItemRequests(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsOfAnotherUser(@RequestHeader(REQUEST_HEADER) int userId) {
        return itemRequestService.findRequestsOfAnotherUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestItemsDto getItemRequestById(@PathVariable(name = "requestId") int requestId) {
        return itemRequestService.findItemRequestById(requestId);
    }

}
