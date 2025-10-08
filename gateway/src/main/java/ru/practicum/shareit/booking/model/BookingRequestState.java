package ru.practicum.shareit.booking.model;

import java.util.Arrays;
import java.util.Optional;

public enum BookingRequestState {

    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingRequestState> from(String bookingRequestState) {
        if (bookingRequestState == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(s -> s.name().equalsIgnoreCase(bookingRequestState))
                .findFirst();
    }
}
