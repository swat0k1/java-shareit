package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDates;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorageDb extends JpaRepository<Booking, Integer> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"bookingUser", "item", "item.ownerUser"})
    List<Booking> findAllByBookingUserIdAndEndBookingIsBeforeOrderByStartBookingDesc(int bookerId, LocalDateTime time);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"bookingUser", "item", "item.ownerUser"})
    List<Booking> findAllByBookingUserIdAndStartBookingIsAfterOrderByStartBookingDesc(int bookerId, LocalDateTime time);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"bookingUser", "item", "item.ownerUser"})
    List<Booking> findAllByBookingUserIdAndBookingStatusOrderByStartBookingDesc(int bookerId, BookingStatus status);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"bookingUser", "item", "item.ownerUser"})
    List<Booking> findAllByItemOwnerUserIdAndEndBookingIsBeforeOrderByStartBookingDesc(int ownerId, LocalDateTime time);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"bookingUser", "item", "item.ownerUser"})
    List<Booking> findAllByItemOwnerUserIdAndStartBookingIsAfterOrderByStartBookingDesc(int ownerId, LocalDateTime time);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"bookingUser", "item", "item.ownerUser"})
    List<Booking> findAllByItemOwnerUserIdAndBookingStatusOrderByStartBookingDesc(int ownerId, BookingStatus status);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"bookingUser", "item", "item.ownerUser"})
    List<Booking> findAllByBookingUserIdAndItemIdAndBookingStatusAndEndBookingIsBefore(int bookerId, int itemId, BookingStatus status, LocalDateTime time);

    @Query("select b from Booking b where b.bookingStatus = 'APPROVED' " +
            "and b.item.id = ?1 " +
            "and(?2 <= b.endBooking and ?3 >= b.startBooking)")
    List<Booking> findAllBookingsWithOverlappingDateRange(@Param("itemId") int itemId, @Param("start") LocalDateTime start,
                                                          @Param("end") LocalDateTime end);

    List<Booking> findAllByBookingUserIdOrderByStartBookingDesc(int bookerId);

    @Query("select b from Booking b where b.bookingUser.id = ?1 and b.startBooking <= ?2 and b.endBooking > ?2 order by b.startBooking desc")
    List<Booking> findAllByBookerIdAndCurrentBookings(@Param("bookerId") int bookerId, @Param("time") LocalDateTime time);

    List<Booking> findAllByItemOwnerUserIdOrderByStartBookingDesc(int ownerId);

    @Query("select b from Booking b where b.item.ownerUser.id = ?1 and b.startBooking <= ?2 and b.endBooking > ?2 order by b.startBooking desc")
    List<Booking> findAllByItemUserIdAndCurrentBookings(@Param("ownerId") int ownerId, @Param("time") LocalDateTime time);

    @Query("select new ru.practicum.shareit.booking.model.BookingDates(b1.item.id, max(b1.endBooking), min(b2.startBooking)) from Booking b1 " +
            "join Booking b2 on b1.item.id = b2.item.id " +
            "where b1.item.id = ?1 and (b1.bookingStatus = 'APPROVED' and b1.endBooking <= ?2) and (b2.bookingStatus = 'APPROVED' and b2.startBooking >= ?2) " +
            "group by b1.item.id")
    BookingDates findBookingDates(@Param("itemId") int itemId, @Param("time") LocalDateTime time);

    @Query("select new ru.practicum.shareit.booking.model.BookingDates(b1.item.id, max(b1.endBooking), min(b2.startBooking)) from Booking b1 " +
            "join Booking b2 on b1.item.id = b2.item.id " +
            "where b1.item.ownerUser.id = ?1 and (b1.bookingStatus = 'APPROVED' and b1.endBooking <= ?2) and (b2.bookingStatus = 'APPROVED' " +
            "and b2.startBooking >= ?2) group by b1.item.id")
    List<BookingDates> findAllBookingsDatesOfUser(@Param("userId") int userId, @Param("time") LocalDateTime time);
}
