package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {

    private int id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    @NotNull
    private int itemId;
    private BookingStatus bookingStatus;

    public BookingCreateDto(int id, LocalDateTime start, LocalDateTime end, int itemId, BookingStatus bookingStatus) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.bookingStatus = bookingStatus;
    }
}
