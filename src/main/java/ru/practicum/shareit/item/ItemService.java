package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.UpdateItem;

@Service
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

        if (item.getOwnerUser() == null || !(item.getOwnerUser().getId() == userId)) {
            throw new PermissionException("Не достаточно прав для обновления объекта или пользователь не был найден");
        }

        updateUsersFields(updateItem, item);
        itemStorage.updateItem(item);
        return ItemMapper.mapToDto(item);

    }

    private Item updateUsersFields(UpdateItem updateItem, Item item) {

        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        }

        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }

        if (updateItem.getIsAvailable() != null) {
            item.setIsAvailable(Boolean.valueOf(updateItem.getIsAvailable()));
        }

        return item;

    }

    public ItemDto getItemById(int id) {
        Item item = itemStorage.getItemById(id);
        return ItemMapper.mapToDto(item);
    }

    // Продолжить тут

}
