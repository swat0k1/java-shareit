package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
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
}
