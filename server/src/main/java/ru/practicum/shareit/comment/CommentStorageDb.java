package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentStorageDb extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByItemId(Integer itemId);

    List<Comment> findAllByItemIdIn(List<Integer> itemsId);

}
