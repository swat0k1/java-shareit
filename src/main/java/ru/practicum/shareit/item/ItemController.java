package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.UpdateItem;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                              @PathVariable(name = "itemId") int itemId,
                              @Valid @RequestBody UpdateItem updateItem) {
        return itemService.update(userId, itemId, updateItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable(name = "itemId") int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getAllUsersItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByQuery(@RequestParam(name = "text", required = false) String query) {
        return itemService.getItemsByQuery(query);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable(name = "itemId") int itemId) {
        return itemService.deleteItem(userId, itemId);
    }
}
