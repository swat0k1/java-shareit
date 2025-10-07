package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(REQUEST_HEADER) Integer requestorId, @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestClient.createItemRequest(requestorId, itemRequestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(REQUEST_HEADER) Integer requestorId) {
        return itemRequestClient.getUserRequests(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsOfAnotherUser(@RequestHeader(REQUEST_HEADER) Integer userId) {
        return itemRequestClient.getRequestsOfAnotherUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable(name = "requestId") Integer requestId) {
        return itemRequestClient.getItemRequestById(requestId);
    }
}
