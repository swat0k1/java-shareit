package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateItem {
    @Size(max = 200)
    private String name;
    @Size(max = 400)
    private String description;
    @Pattern(regexp = "^true?$|^false?$")
    private String available;
}
