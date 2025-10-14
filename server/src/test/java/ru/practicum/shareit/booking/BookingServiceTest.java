package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.UpdateItem;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    private UserDto owner;
    private UserDto booker;
    private ItemDto item;
    private BookingDto booking;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        owner = new UserDto(0, "Owner", "owner@owner.ru");
        booker = new UserDto(0, "Booker", "booker@booker.ru");
        item = new ItemDto(0, "Test", "Test", "true", 0, null);
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        item = itemService.create(owner.getId(), item);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endTime = startTime.plusDays(1);
        bookingCreateDto = new BookingCreateDto(0, startTime, endTime, item.getId(), null);
        booking = bookingService.create(booker.getId(), bookingCreateDto);
    }

    @Test
    void createBookingSuccessfully() {
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStart()).isEqualTo(bookingCreateDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingCreateDto.getEnd());
        assertThat(booking.getItem().getName()).isEqualTo(item.getName());
        assertThat(booking.getBooker().getName()).isEqualTo(booker.getName());
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void createBookingWithEqualStartAndEnd() {
        bookingCreateDto.setEnd(bookingCreateDto.getStart());
        WrongDataException exception = assertThrows(WrongDataException.class,
                () -> bookingService.create(booker.getId(), bookingCreateDto));
        assertThat(exception.getMessage()).isEqualTo("Дата старта и конца бронирования совпадают");
    }

    @Test
    void createBookingWithNotAvailableItem() {
        UpdateItem updateItem = new UpdateItem();
        updateItem.setAvailable("false");
        itemService.update(owner.getId(), item.getId(), updateItem);
        WrongDataException exception = assertThrows(WrongDataException.class,
                () -> bookingService.create(booker.getId(), bookingCreateDto));
        assertThat(exception.getMessage()).isEqualTo("Предмет не доступен для бронирования");
    }

    @Test
    void createBookingWithOverlappingDate() {
        bookingService.updateStatus(owner.getId(), booking.getId(), true);
        WrongDataException exception = assertThrows(WrongDataException.class,
                () -> bookingService.create(booker.getId(), bookingCreateDto));
        assertThat(exception.getMessage()).isEqualTo("Пересекаются даты бронирования");
    }

    @Test
    void updateStatusToApprovedSuccessfully() {
        BookingDto updatedBooking = bookingService.updateStatus(owner.getId(), booking.getId(), true);
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void updateStatusToRejectedSuccessfully() {
        BookingDto updatedBooking = bookingService.updateStatus(owner.getId(), booking.getId(), false);
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void updateStatusWithNonExistentUser() {
        WrongUserException exception = assertThrows(WrongUserException.class,
                () -> bookingService.updateStatus(999, booking.getId(), true));
        assertThat(exception.getMessage()).isEqualTo("Недостаточно прав для обновления бронирования");
    }

    @Test
    void findBookingByIdSuccessfully() {
        BookingDto result = bookingService.findById(owner.getId(), booking.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getItem().getName()).isEqualTo(item.getName());
        assertThat(result.getBooker().getName()).isEqualTo(booker.getName());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void findBookingByIdNotExistThrowsException() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(booker.getId(), 999));
        assertThat(exception.getMessage()).isEqualTo("Not found exception: Бронирование не найдено");
    }

    @Test
    void findBookingByIdWithoutAccessRightsThrowsException() {
        PermissionException exception = assertThrows(PermissionException.class,
                () -> bookingService.findById(999, booking.getId()));
        assertThat(exception.getMessage()).isEqualTo("Permission exception: Недостаточно прав для поиска бронирования");
    }

    @Test
    void findAllBookingsOfBookerWithStateAll() {
        List<BookingDto> bookings = bookingService.findAllUsersBookings(booker.getId(), BookingRequestState.ALL);
        assertThat(bookings).isNotNull();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findAllBookingsOfBookerWithStateFuture() {
        List<BookingDto> futureBookings = bookingService.findAllUsersBookings(booker.getId(), BookingRequestState.FUTURE);
        assertThat(futureBookings).isNotNull();
        assertThat(futureBookings.size()).isEqualTo(1);
        assertThat(futureBookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findAllBookingsOfBookerWithStatePast() {
        LocalDateTime pastStart = LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime pastEnd = pastStart.plusDays(1);
        BookingCreateDto pastBooking = new BookingCreateDto(0, pastStart, pastEnd, item.getId(), null);
        bookingService.create(booker.getId(), pastBooking);

        List<BookingDto> pastBookings = bookingService.findAllUsersBookings(booker.getId(), BookingRequestState.PAST);
        assertThat(pastBookings).isNotNull();
        assertThat(pastBookings.size()).isEqualTo(1);
    }

    @Test
    void findAllBookingsOfOwnerWithStateAll() {
        List<BookingDto> ownerBookings = bookingService.findAllBookingsOfOwner(owner.getId(), BookingRequestState.ALL);
        assertThat(ownerBookings).isNotNull();
        assertThat(ownerBookings.size()).isEqualTo(1);
        assertThat(ownerBookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findAllBookingsOfOwnerWithStateRejected() {
        List<BookingDto> rejectedBookings = bookingService.findAllBookingsOfOwner(owner.getId(), BookingRequestState.REJECTED);
        assertThat(rejectedBookings).isNotNull();
        assertThat(rejectedBookings.size()).isEqualTo(0);
    }

    @Test
    void findAllBookingsOfOwnerWithStateFuture() {
        List<BookingDto> futureBookings = bookingService.findAllBookingsOfOwner(owner.getId(), BookingRequestState.FUTURE);
        assertThat(futureBookings).isNotNull();
        assertThat(futureBookings.size()).isEqualTo(1);
        assertThat(futureBookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findAllBookingsOfOwnerWithStatePast() {
        List<BookingDto> pastBookings = bookingService.findAllBookingsOfOwner(owner.getId(), BookingRequestState.PAST);
        assertThat(pastBookings).isNotNull();
        assertThat(pastBookings.size()).isEqualTo(0);
    }
}