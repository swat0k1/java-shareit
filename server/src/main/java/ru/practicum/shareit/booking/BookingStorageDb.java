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

    /*
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"bookingUser", "item", "item.ownerUser"})
    List<Booking> findAllByBookingUserIdAndItemIdAndBookingStatusAndEndBookingIsBefore(int bookerId, int itemId, BookingStatus status, LocalDateTime time);
     */

    @Query("SELECT b FROM Booking b " +
            "JOIN b.bookingUser u " +
            "JOIN b.item i " +
            "JOIN i.ownerUser o " +
            "WHERE u.id = :bookerId " +
            "AND i.id = :itemId " +
            "AND b.bookingStatus = :status " +
            "AND b.endBooking < :time")
    List<Booking> findAllByBookingUserIdAndItemIdAndBookingStatusAndEndBookingIsBefore(
            @Param("bookerId") int bookerId,
            @Param("itemId") int itemId,
            @Param("status") BookingStatus status,
            @Param("time") LocalDateTime time
    );

    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = 'APPROVED' " +
            "AND b.item.id = ?1 " +
            "AND(?2 <= b.endBooking AND ?3 >= b.startBooking)")
    List<Booking> findAllBookingsWithOverlappingDateRange(@Param("itemId") int itemId, @Param("start") LocalDateTime start,
                                                          @Param("end") LocalDateTime end);

    List<Booking> findAllByBookingUserIdOrderByStartBookingDesc(int bookerId);

    @Query("SELECT b FROM Booking b WHERE b.bookingUser.id = ?1 AND b.startBooking <= ?2 AND b.endBooking > ?2 ORDER BY b.startBooking DESC")
    List<Booking> findAllByBookerIdAndCurrentBookings(@Param("bookerId") int bookerId, @Param("time") LocalDateTime time);

    List<Booking> findAllByItemOwnerUserIdOrderByStartBookingDesc(int ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerUser.id = ?1 AND b.startBooking <= ?2 AND b.endBooking > ?2 ORDER BY b.startBooking DESC")
    List<Booking> findAllByItemUserIdAndCurrentBookings(@Param("ownerId") int ownerId, @Param("time") LocalDateTime time);

    @Query("SELECT new ru.practicum.shareit.booking.model.BookingDates(b1.item.id, MAX(b2.startBooking), MIN(b3.endBooking)) " +
            "FROM Booking b1 " +
            "LEFT JOIN Booking b2 ON b1.item.id = b2.item.id AND b2.bookingStatus = 'APPROVED' AND b2.startBooking >= ?2 " +
            "LEFT JOIN Booking b3 ON b1.item.id = b3.item.id AND b3.bookingStatus = 'APPROVED' AND b3.endBooking <= ?2 " +
            "WHERE b1.item.id = ?1 " +
            "GROUP BY b1.item.id")
    BookingDates findBookingDates(@Param("itemId") int itemId, @Param("time") LocalDateTime time);

    @Query("SELECT new ru.practicum.shareit.booking.model.BookingDates(b1.item.id, MAX(b3.endBooking), MIN(b2.startBooking)) " +
            "FROM Booking b1 " +
            "LEFT JOIN Booking b2 ON b1.item.id = b2.item.id " +
            "AND b2.bookingStatus = 'APPROVED' AND b2.startBooking >= ?2 " +
            "LEFT JOIN Booking b3 ON b1.item.id = b3.item.id " +
            "AND b3.bookingStatus = 'APPROVED' AND b3.endBooking <= ?2 " +
            "WHERE b1.item.ownerUser.id = ?1 " +
            "GROUP BY b1.item.id")
    List<BookingDates> findAllBookingsDatesOfUser(@Param("userId") int userId, @Param("time") LocalDateTime time);
}
