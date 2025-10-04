package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.UpdateItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {

    private final ItemStorage itemStorage;

    public ItemService(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public ItemDto create(int id, ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemDto);
        itemStorage.createItem(id, item);
        return ItemMapper.mapToDto(item);
    }

    public ItemDto update(int userId, int itemId, UpdateItem updateItem) {
        Item item = itemStorage.getItemById(itemId);

        if (item.getOwnerUser() == null || (item.getOwnerUser().getId() != userId)) {
            throw new PermissionException("Не достаточно прав для обновления объекта или пользователь не был найден");
        }

        updateUsersFields(updateItem, item);
        itemStorage.updateItem(item);
        return ItemMapper.mapToDto(item);

    }

    private void updateUsersFields(UpdateItem updateItem, Item item) {

        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        }

        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }

        if (updateItem.getAvailable() != null) {
            item.setAvailable(Boolean.valueOf(updateItem.getAvailable()));
        }

    }

    public ItemDto getItemById(int id) {
        Item item = itemStorage.getItemById(id);
        return ItemMapper.mapToDto(item);
    }

    public List<ItemDto> getAllUsersItems(int id) {
        return itemStorage.getAllUsersItems(id)
                .stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    public List<ItemDto> getItemsByQuery(String query) {
        if (query.isBlank()) {
            return new ArrayList<ItemDto>();
        }
        String[] queryArray = query.trim().split("[,.]");
        Set<String> querySet = Arrays.stream(queryArray)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        List<Item> items = itemStorage.getByQuery(querySet);
        return items.stream().map(ItemMapper::mapToDto).toList();
    }

    public ItemDto deleteItem(int userId, int itemId) {
        Item item = itemStorage.getItemById(itemId);
        if (item.getOwnerUser().getId() != userId) {
            throw new PermissionException("Не достаточно прав для удаления объекта");
        }
        itemStorage.deleteItem(itemId);
        return ItemMapper.mapToDto(item);
    }

}
