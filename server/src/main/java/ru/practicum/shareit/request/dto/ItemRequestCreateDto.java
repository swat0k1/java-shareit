package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class ItemRequestCreateDto {
    private String description;
}
