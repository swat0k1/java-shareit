package ru.practicum.shareit.user.model;

import lombok.Data;

import java.util.Objects;

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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(getEmail(), user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getEmail());
    }

}
