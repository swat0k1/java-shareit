package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static Comment mapToComment(CreateCommentDto dto, User bookingUser, Item item) {

        Comment comment = new Comment();
        comment.setCommentText(dto.getText());
        comment.setItem(item);
        comment.setCommentAuthor(bookingUser);
        comment.setCreationTime(LocalDateTime.now());
        return comment;

    }

    public static CommentDto mapToCommentDto(Comment comment) {

        return new CommentDto(comment.getId(), comment.getCommentText(),
                comment.getItem().getId(), comment.getCommentAuthor().getName(), comment.getCreationTime());

    }

    public static List<CommentDto> mapToCommentDtos(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

}
