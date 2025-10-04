package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.ItemStorageDb;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorageDb;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookingService {

    private final BookingStorageDb bookingStorageDb;
    private final ItemStorageDb itemStorageDb;
    private final UserStorageDb userStorageDb;

    public BookingService(BookingStorageDb bookingStorageDb, ItemStorageDb itemStorageDb, UserStorageDb userStorageDb) {
        this.bookingStorageDb = bookingStorageDb;
        this.itemStorageDb = itemStorageDb;
        this.userStorageDb = userStorageDb;
    }

    @Transactional
    public BookingDto create(int bookerId, BookingCreateDto bookingCreateDto) {

        checkBookingDate(bookingCreateDto);

        User booker = findUserById(bookerId);
        Item item = findAvailableItemById(bookingCreateDto.getItemId());

        checkOverlappingBookings(item, bookingCreateDto.getStart(), bookingCreateDto.getEnd());

        Booking booking = BookingMapper.mapBookingCreateToBooking(bookingCreateDto, item, booker);
        return BookingMapper.mapBookingToBookingDto(bookingStorageDb.save(booking));
    }

    @Transactional
    public BookingDto updateStatus(int userId, int bookingId, Boolean approved) {
        Booking booking = findBookingById(bookingId);

        if (!hasAccessToUpdate(booking, userId)) {
            throw new ValidationException("Недостаточно прав для обновления бронирования");
        }

        if (approved) {
            checkOverlappingBookings(booking.getItem(), booking.getStartBooking(), booking.getEndBooking());
            booking.setBookingStatus(BookingStatus.APPROVED);
        } else {
            updateBookingStatus(booking, userId);
        }

        return BookingMapper.mapBookingToBookingDto(bookingStorageDb.save(booking));
    }

    public BookingDto findById(int userId, int bookingId) {
        Booking booking = findBookingById(bookingId);

        if (!canAccessBooking(booking, userId)) {
            throw new PermissionException("Недостаточно прав для поиска бронирования");
        }

        return BookingMapper.mapBookingToBookingDto(booking);
    }

    public List<BookingDto> findAllUsersBookings(int bookerId, BookingRequestState state) {
        findUserById(bookerId);
        List<Booking> bookings = getBookingsByStateForBooker(bookerId, state);
        return BookingMapper.mapBookingsToBookingDtos(bookings);
    }

    public List<BookingDto> findAllBookingsOfOwner(int ownerId, BookingRequestState state) {
        findUserById(ownerId);
        List<Booking> bookings = getBookingsByStateForOwner(ownerId, state);
        return BookingMapper.mapBookingsToBookingDtos(bookings);
    }

    private void checkBookingDate(BookingCreateDto bookingCreateDto) {
        if (bookingCreateDto.getStart().equals(bookingCreateDto.getEnd())) {
            throw new ValidationException("Дата старта и конца бронирования совпадают");
        }
    }

    private User findUserById(int userId) {
        return userStorageDb.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item findAvailableItemById(int itemId) {
        Item item = itemStorageDb.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет не доступен для бронирования");
        }
        return item;
    }

    private void checkOverlappingBookings(Item item, LocalDateTime start, LocalDateTime end) {
        List<Booking> overlappingBookings = bookingStorageDb
                .findAllBookingsWithOverlappingDateRange(item.getId(), start, end);
        if (!overlappingBookings.isEmpty()) {
            throw new ValidationException("Пересекаются даты бронирования");
        }
    }

    private Booking findBookingById(int bookingId) {
        return bookingStorageDb.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    private boolean hasAccessToUpdate(Booking booking, int userId) {
        return checkIdOfItemOwner(booking, userId);
    }

    private boolean canAccessBooking(Booking booking, int userId) {
        return checkIdOfItemOwner(booking, userId) || booking.getBookingUser().getId() == userId;
    }

    private void updateBookingStatus(Booking booking, int userId) {
        if (checkIdOfItemOwner(booking, userId)) {
            booking.setBookingStatus(BookingStatus.REJECTED);
        } else if (booking.getBookingUser().getId() == userId) {
            booking.setBookingStatus(BookingStatus.CANCELED);
        } else {
            throw new PermissionException("Недостаточно прав для обновления бронирования");
        }
    }

    private List<Booking> getBookingsByStateForBooker(int bookerId, BookingRequestState bookingRequestState) {
        LocalDateTime now = LocalDateTime.now();
        return switch (bookingRequestState) {
            case ALL -> bookingStorageDb.findAllByBookingUserIdOrderByStartBookingDesc(bookerId);
            case CURRENT -> bookingStorageDb.findAllByBookerIdAndCurrentBookings(bookerId, now);
            case PAST -> bookingStorageDb.findAllByBookingUserIdAndEndBookingIsBeforeOrderByStartBookingDesc(bookerId, now);
            case FUTURE -> bookingStorageDb.findAllByBookingUserIdAndStartBookingIsAfterOrderByStartBookingDesc(bookerId, now);
            case WAITING -> bookingStorageDb.findAllByBookingUserIdAndBookingStatusOrderByStartBookingDesc(bookerId, BookingStatus.WAITING);
            case REJECTED -> bookingStorageDb.findAllByBookingUserIdAndBookingStatusOrderByStartBookingDesc(bookerId, BookingStatus.REJECTED);
        };
    }

    private List<Booking> getBookingsByStateForOwner(int ownerId, BookingRequestState bookingRequestState) {
        LocalDateTime now = LocalDateTime.now();
        return switch (bookingRequestState) {
            case ALL -> bookingStorageDb.findAllByItemOwnerUserIdOrderByStartBookingDesc(ownerId);
            case CURRENT -> bookingStorageDb.findAllByItemUserIdAndCurrentBookings(ownerId, now);
            case PAST -> bookingStorageDb.findAllByItemOwnerUserIdAndEndBookingIsBeforeOrderByStartBookingDesc(ownerId, now);
            case FUTURE -> bookingStorageDb.findAllByItemOwnerUserIdAndStartBookingIsAfterOrderByStartBookingDesc(ownerId, now);
            case WAITING -> bookingStorageDb.findAllByItemOwnerUserIdAndBookingStatusOrderByStartBookingDesc(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingStorageDb.findAllByItemOwnerUserIdAndBookingStatusOrderByStartBookingDesc(ownerId, BookingStatus.REJECTED);
        };
    }

    private boolean checkIdOfItemOwner(Booking booking, int userId) {
        Integer itemOwnerId = Optional.of(booking.getItem().getOwnerUser().getId())
                .orElseThrow(() -> new ValidationException("Неверный id пользователя"));
        return userId == itemOwnerId;
    }
}
