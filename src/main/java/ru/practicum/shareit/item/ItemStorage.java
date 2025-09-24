package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Slf4j
public class ItemStorage {

    private int id = 1;
    private Map<Integer, Item> items = new HashMap<>();
    private final UserStorage userStorage;

    public ItemStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private int getId() {
        return id++;
    }

    public Item createItem(int userId, Item item) {
        User user = userStorage.getUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь не был найден");
        }

        item.setId(getId());
        item.setOwnerUser(user);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item) {
        return items.put(item.getId(), item);
    }

    public Item getItemById(int id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException("Предмет id = " + id + " не найден!");
        }
        return item;
    }

    public List<Item> getAllUsersItems(int id) {
        return items.values()
                .stream()
                .filter(item -> item.getOwnerUser().getId() == id)
                .toList();
    }

    public List<Item> getByQuery(Set<String> text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> text.stream().anyMatch(string ->
                        item.getName().toLowerCase().contains(string.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(string.toLowerCase())
                )).toList();
    }

    public void deleteItem(int id) {

        Item item = items.remove(id);

        if (item == null) {
            throw new NotFoundException("Предмет id = " + id + " не найден!");
        }
    }
}