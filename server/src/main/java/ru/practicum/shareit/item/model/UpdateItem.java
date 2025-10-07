package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class UpdateItem {
    private String name;
    private String description;
    private String available;
}
