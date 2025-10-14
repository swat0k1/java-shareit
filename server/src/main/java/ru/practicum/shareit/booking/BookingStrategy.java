package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStrategy {

    List<Booking> find(int bookerId, LocalDateTime currentTimeForBookingCheck);

}
