package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUser {

    @Email
    private String email;
    private String name;

    public UpdateUser(String email, String name) {

        this.email = email;
        this.name = name;

    }

}


