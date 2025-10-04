package ru.practicum.shareit.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private int id;
    private String text;
    private int itemId;
    private String authorName;
    private LocalDateTime created;

    public CommentDto(int id, String text, int itemId, String authorName, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.itemId = itemId;
        this.authorName = authorName;
        this.created = created;
    }
}
