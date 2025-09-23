package ru.practicum.shareit.user.model;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {

    private int id;
    private String email;
    private String name;

    public User(int id, String email, String name) {

        this.id = id;
        this.email = email;
        this.name = name;

    }

}
