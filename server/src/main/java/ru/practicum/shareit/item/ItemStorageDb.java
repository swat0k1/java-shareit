package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorageDb extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerUserId(int userId);

    @Query("select i from Item i where (lower(i.name) like lower(?1) " +
            "or lower(i.description) like lower(?1)) and i.available = true")
    List<Item> findByQueryText(String text);

    List<Item> findAllByItemRequestIdIn(List<Integer> requestIds);

    List<Item> findAllByItemRequestId(Integer requestId);
}
