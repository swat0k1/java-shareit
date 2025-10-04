package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemDto {

    private int id;
    @NotBlank
    @Size(max = 200)
    private String name;
    @NotBlank
    @Size(max = 400)
    private String description;
    @NotBlank
    @Pattern(regexp = "^true?$|^false?$")
    private String available;
    private User ownerUser;

    public ItemDto(int id, String name, String description, String available, User ownerUser) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerUser = ownerUser;

    }
}
