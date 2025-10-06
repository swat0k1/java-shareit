package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserDataException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Deprecated
@Repository
@Slf4j
public class UserStorage {

    private int id = 1;
    private Map<Integer, User> users = new HashMap<>();

    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь id = " + id + " не найден!");
        }
        return user;
    }

    public User createUser(User user) {

        boolean exists = users.values()
                .stream()
                .anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()));

        if (exists) {
            throw new UserDataException("Данный Email уже используется");
        }

        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    private int getId() {
        return id++;
    }

    public List<User> getUsers() {
        return users.values().stream().toList();
    }

    public User updateUser(User user) {

        boolean exists = users.values()
                .stream()
                .anyMatch(existingUser -> existingUser.equals(user)
                        && !Objects.equals(existingUser.getId(), user.getId()));

        if (exists) {
            throw new UserDataException("Данный Email уже используется");
        }

        return users.put(user.getId(), user);
    }

    public User deleteUser(int id) {

        User user = users.remove(id);

        if (user == null) {
            throw new NotFoundException("Пользователь id = " + id + " не найден!");
        }

        return user;
    }

}
