package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BookingCreateDto {

    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int itemId;
    private BookingStatus bookingStatus;

}
