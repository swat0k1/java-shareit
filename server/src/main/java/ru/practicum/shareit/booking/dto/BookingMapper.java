package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking mapBookingCreateToBooking(BookingCreateDto bookingCreateDto, Item item, User bookingUser) {

        BookingStatus status = Optional.ofNullable(bookingCreateDto.getBookingStatus()).orElse(BookingStatus.WAITING);

        return new Booking(bookingCreateDto.getId(), bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(), item, bookingUser, status
        );

    }

    public static BookingDto mapBookingToBookingDto(Booking booking) {

        return new BookingDto(booking.getId(), booking.getStartBooking(), booking.getEndBooking(),
                booking.getItem(), booking.getBookingUser(), booking.getBookingStatus()
        );
    }

    public static List<BookingDto> mapBookingsToBookingDtos(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::mapBookingToBookingDto)
                .collect(Collectors.toList());
    }

}
