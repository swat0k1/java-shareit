package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestStorageDb extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Integer userId);

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Integer requestorId);
}
