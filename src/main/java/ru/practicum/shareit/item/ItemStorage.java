package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
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
        item.setId(getId());
        item.setOwnerUser(user);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item) {
        return items.put(item.getId(), item);
    }

    public Item getItemById(int id) {
        try {
            return items.get(id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Предмет id = " + id + " не найден!");
        }
    }

    public List<Item> getAllUsersItems(int id) {
        return items.values()
                .stream()
                .filter(item -> item.getOwnerUser().getId() == id)
                .toList();
    }

    public List<Item> getByQuery(Set<String> text) {
        return items.values().stream()
                .filter(Item::getIsAvailable)
                .filter(item -> text.stream().anyMatch(string ->
                        item.getName().matches("(?i)" + string) || item.getDescription().matches("(?i)" + string)))
                .toList();
    }

    public void deleteItem(int id) {
        items.remove(id);
    }
}