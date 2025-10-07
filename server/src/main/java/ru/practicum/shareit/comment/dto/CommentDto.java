package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CommentDto {

    private int id;
    private String text;
    private int itemId;
    private String authorName;
    private LocalDateTime created;
}
