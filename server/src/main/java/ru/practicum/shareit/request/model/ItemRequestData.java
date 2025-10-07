package ru.practicum.shareit.request.model;

import lombok.Data;

@Data
public class ItemRequestData {
    private int itemId;
    private String name;
    private int ownerId;
}
