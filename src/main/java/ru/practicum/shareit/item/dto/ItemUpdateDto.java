package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemUpdateDto {

    @Size(max = 200)
    private String name;
    @Size(max = 400)
    private String description;
    @Pattern(regexp = "^true?$|^false?$")
    private String available;

    public ItemUpdateDto(String name, String description, String available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
