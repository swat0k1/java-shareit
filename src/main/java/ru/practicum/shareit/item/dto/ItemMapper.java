package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto mapToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable().toString(),
                item.getOwnerUser());
    }

    public static Item mapToItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                Boolean.valueOf(itemDto.getAvailable()), itemDto.getOwnerUser());
    }

}
