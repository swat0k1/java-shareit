package ru.practicum.shareit.booking;

import jakarta.annotation.PostConstruct;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingStorageDb bookingStorageDb;
    private final ItemStorageDb itemStorageDb;
    private final UserStorageDb userStorageDb;
    private final Map<BookingRequestState, BookingStrategy> bookerStrategyMapForBooker = new EnumMap<>(BookingRequestState.class);
    private final Map<BookingRequestState, BookingStrategy> bookerStrategyMapForOwner = new EnumMap<>(BookingRequestState.class);

    public BookingService(BookingStorageDb bookingStorageDb, ItemStorageDb itemStorageDb, UserStorageDb userStorageDb) {
        this.bookingStorageDb = bookingStorageDb;
        this.itemStorageDb = itemStorageDb;
        this.userStorageDb = userStorageDb;
    }

    @PostConstruct
    private void initBookerStrategiesForBooker() {
        bookerStrategyMapForBooker.put(BookingRequestState.ALL,
                (bookerId, now) -> bookingStorageDb.findAllByBookingUserIdOrderByStartBookingDesc(bookerId));
        bookerStrategyMapForBooker.put(BookingRequestState.CURRENT,
                (bookerId, now) -> bookingStorageDb.findAllByBookerIdAndCurrentBookings(bookerId, now));
        bookerStrategyMapForBooker.put(BookingRequestState.PAST,
                (bookerId, now) -> bookingStorageDb.findAllByBookingUserIdAndEndBookingIsBeforeOrderByStartBookingDesc(bookerId, now));
        bookerStrategyMapForBooker.put(BookingRequestState.FUTURE,
                (bookerId, now) -> bookingStorageDb.findAllByBookingUserIdAndStartBookingIsAfterOrderByStartBookingDesc(bookerId, now));
        bookerStrategyMapForBooker.put(BookingRequestState.WAITING,
                (bookerId, now) -> bookingStorageDb.findAllByBookingUserIdAndBookingStatusOrderByStartBookingDesc(bookerId, BookingStatus.WAITING));
        bookerStrategyMapForBooker.put(BookingRequestState.REJECTED,
                (bookerId, now) -> bookingStorageDb.findAllByBookingUserIdAndBookingStatusOrderByStartBookingDesc(bookerId, BookingStatus.REJECTED));
    }

    @PostConstruct
    private void initBookerStrategiesForOwner() {
        bookerStrategyMapForOwner.put(BookingRequestState.ALL,
                (ownerId, now) -> bookingStorageDb.findAllByItemOwnerUserIdOrderByStartBookingDesc(ownerId));
        bookerStrategyMapForOwner.put(BookingRequestState.CURRENT,
                (ownerId, now) -> bookingStorageDb.findAllByItemUserIdAndCurrentBookings(ownerId, now));
        bookerStrategyMapForOwner.put(BookingRequestState.PAST,
                (ownerId, now) -> bookingStorageDb.findAllByItemOwnerUserIdAndEndBookingIsBeforeOrderByStartBookingDesc(ownerId, now));
        bookerStrategyMapForOwner.put(BookingRequestState.FUTURE,
                (ownerId, now) -> bookingStorageDb.findAllByItemOwnerUserIdAndStartBookingIsAfterOrderByStartBookingDesc(ownerId, now));
        bookerStrategyMapForOwner.put(BookingRequestState.WAITING,
                (ownerId, now) -> bookingStorageDb.findAllByItemOwnerUserIdAndBookingStatusOrderByStartBookingDesc(ownerId, BookingStatus.WAITING));
        bookerStrategyMapForOwner.put(BookingRequestState.REJECTED,
                (ownerId, now) -> bookingStorageDb.findAllByItemOwnerUserIdAndBookingStatusOrderByStartBookingDesc(ownerId, BookingStatus.REJECTED));
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
        LocalDateTime currentTimeForBookingCheck = LocalDateTime.now();
        BookingStrategy strategy = bookerStrategyMapForBooker.get(bookingRequestState);
        if (strategy == null) {
            throw new IllegalArgumentException("Неверный статус запроса бронирования!");
        }
        return strategy.find(bookerId, currentTimeForBookingCheck);
    }

    private List<Booking> getBookingsByStateForOwner(int ownerId, BookingRequestState bookingRequestState) {
        LocalDateTime currentTimeForBookingCheck = LocalDateTime.now();
        BookingStrategy strategy = bookerStrategyMapForOwner.get(bookingRequestState);
        if (strategy == null) {
            throw new IllegalArgumentException("Неверный статус запроса бронирования!");
        }
        return strategy.find(ownerId, currentTimeForBookingCheck);
    }

    private boolean checkIdOfItemOwner(Booking booking, int userId) {
        Integer itemOwnerId = Optional.of(booking.getItem().getOwnerUser().getId())
                .orElseThrow(() -> new ValidationException("Неверный id пользователя"));
        return userId == itemOwnerId;
    }
}
