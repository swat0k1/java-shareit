package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDates {

    private int itemId;
    private LocalDateTime previousBooking;
    private LocalDateTime nextBooking;

    public BookingDates(int itemId, LocalDateTime previousBooking, LocalDateTime nextBooking) {
        this.itemId = itemId;
        this.previousBooking = previousBooking;
        this.nextBooking = nextBooking;
    }
}
