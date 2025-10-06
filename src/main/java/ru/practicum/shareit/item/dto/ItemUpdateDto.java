package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemUpdateDto {

    @Size(max = 200)
    private String name;
    @Size(max = 400)
    private String description;
    @Pattern(regexp = "^true?$|^false?$")
    private String available;
}
