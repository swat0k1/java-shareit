package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class Item {

    private int id;
    private String name;
    private String description;
    private Boolean available;
    private User ownerUser;
    private ItemRequest itemRequest;

    public Item(int id, String name, String description, Boolean available, User ownerUser, ItemRequest itemRequest) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerUser = ownerUser;
        this.itemRequest = itemRequest;
    }

    public Item(int id, String name, String description, Boolean available, User ownerUser) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerUser = ownerUser;
    }

    public Item(String name, String description, Boolean available, User ownerUser, ItemRequest itemRequest) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerUser = ownerUser;
        this.itemRequest = itemRequest;
    }

}
