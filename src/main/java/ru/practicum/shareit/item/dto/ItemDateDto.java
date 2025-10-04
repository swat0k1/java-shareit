package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
public class ItemDateDto {

    private int id;
    private String name;
    private String description;
    private Boolean available;
    private int userId;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments;

    @Override
    public String toString() {
        return "ItemDateDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", userId=" + userId +
                ", previousBooking=" + lastBooking +
                ", nextBooking=" + nextBooking +
                ", commentDtos=" + comments +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ItemDateDto that = (ItemDateDto) object;
        return getId() == that.getId() && getUserId() == that.getUserId() && Objects.equals(getName(),
                that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getAvailable(),
                that.getAvailable()) && Objects.equals(getLastBooking(), that.getLastBooking()) && Objects.equals(getNextBooking(),
                that.getNextBooking()) && Objects.equals(getComments(), that.getComments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getAvailable(), getUserId(), getLastBooking(),
                getNextBooking(), getComments());
    }
}
