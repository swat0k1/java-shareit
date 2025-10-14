package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User ownerUser;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;

    public Item() {
    }

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
