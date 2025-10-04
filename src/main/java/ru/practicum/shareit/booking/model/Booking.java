package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "start_date")
    private LocalDateTime startBooking;
    @Column(name = "end_date")
    private LocalDateTime endBooking;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User bookingUser;
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    public Booking() {

    }

    public Booking(int id, LocalDateTime startBooking, LocalDateTime endBooking, Item item, User bookingUser, BookingStatus bookingStatus) {
        this.id = id;
        this.startBooking = startBooking;
        this.endBooking = endBooking;
        this.item = item;
        this.bookingUser = bookingUser;
        this.bookingStatus = bookingStatus;
    }
}
