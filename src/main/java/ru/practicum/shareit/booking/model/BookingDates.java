package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDates {

    private int itemId;
    private LocalDateTime previousBooking;
    private LocalDateTime nextBooking;
}
