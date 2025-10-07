package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.practicum.shareit.request.model.ItemRequestData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class ItemRequestItemsDto {
    private int id;
    private String description;
    private Integer requestorId;
    private LocalDateTime created;
    private List<ItemRequestData> items = new ArrayList<>();
}
