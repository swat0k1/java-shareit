package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {

    private int id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;

    public UserDto(int id, String email, String name) {

        this.id = id;
        this.email = email;
        this.name = name;

    }

}
