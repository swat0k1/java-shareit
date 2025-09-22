package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
public class Item {

    private int id;
    private String name;
    private String description;
    private Boolean isAvailable;
    private User ownerUser;
    private ItemRequest itemRequest;

    public Item(int id, String name, String description, Boolean isAvailable, User ownerUser, ItemRequest itemRequest) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.ownerUser = ownerUser;
        this.itemRequest = itemRequest;
    }

    public Item(int id, String name, String description, Boolean isAvailable, User ownerUser) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.ownerUser = ownerUser;
    }

    public Item(String name, String description, Boolean isAvailable, User ownerUser, ItemRequest itemRequest) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.ownerUser = ownerUser;
        this.itemRequest = itemRequest;
    }

}
