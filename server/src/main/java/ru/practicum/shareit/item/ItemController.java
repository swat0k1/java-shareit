package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.UpdateItem;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(REQUEST_HEADER) int userId, @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(REQUEST_HEADER) int userId,
                              @PathVariable(name = "itemId") int itemId,
                              @RequestBody UpdateItem updateItem) {
        return itemService.update(userId, itemId, updateItem);
    }

    @GetMapping("/{itemId}")
    public ItemDateDto getItemById(@PathVariable(name = "itemId") int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDateDto> getAllUsersItems(@RequestHeader(REQUEST_HEADER) int userId) {
        return itemService.getAllUsersItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByQuery(@RequestParam(name = "text", required = false) String query) {
        return itemService.getItemsByQuery(query);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@RequestHeader(REQUEST_HEADER) int userId, @PathVariable(name = "itemId") int itemId) {
        return itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(REQUEST_HEADER) int bookerId, @PathVariable(name = "itemId") int itemId,
                                @RequestBody CreateCommentDto commentDto) {
        return itemService.addComment(bookerId, itemId, commentDto);
    }
}
