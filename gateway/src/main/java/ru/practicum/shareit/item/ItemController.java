package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.UpdateItem;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemClient itemClient;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(REQUEST_HEADER) Integer userId, @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(REQUEST_HEADER) Integer userId, @PathVariable(name = "itemId") Integer itemId,
            @Valid @RequestBody UpdateItem updateItem) {
        return itemClient.updateItem(userId, itemId, updateItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable(name = "itemId") Integer itemId) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(REQUEST_HEADER) Integer userId) {
        return itemClient.getAllUsersItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByQuery(@RequestHeader(REQUEST_HEADER) Integer userId, @RequestParam(name = "text", required = false) String query) {
        return itemClient.getByQuery(userId, query);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(REQUEST_HEADER) Integer userId, @PathVariable(name = "itemId") Integer itemId) {
        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(REQUEST_HEADER) Integer bookerId, @PathVariable(name = "itemId") Integer itemId,
            @Valid @RequestBody CreateCommentDto createCommentDto) {
        return itemClient.createComment(bookerId, itemId, createCommentDto);
    }
}
