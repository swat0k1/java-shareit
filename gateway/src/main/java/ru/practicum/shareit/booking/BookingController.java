package ru.practicum.shareit.booking;


import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingRequestState;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(REQUEST_HEADER) Integer bookerId, @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        return bookingClient.createBooking(bookerId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader(REQUEST_HEADER) Integer userId, @PathVariable(name = "bookingId") Integer bookingId,
                                               @RequestParam(name = "approved") Boolean approved) {
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(REQUEST_HEADER) Integer userId, @PathVariable(name = "bookingId") Integer bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsOfBooker(@RequestHeader(REQUEST_HEADER) Integer userId,
                                                          @RequestParam(name = "state", required = false, defaultValue = "all") String stateParam) {
        BookingRequestState state = BookingRequestState.from(stateParam).orElseThrow(() -> new ValidationException("Ошибочное состояние бронирования"));
        return bookingClient.getAllBookingsOfBooker(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsOfOwner(@RequestHeader(REQUEST_HEADER) Integer ownerId,
                                                         @RequestParam(name = "state", required = false, defaultValue = "all") String stateParam) {
        BookingRequestState state = BookingRequestState.from(stateParam).orElseThrow(() -> new ValidationException("Ошибочное состояние бронирования"));
        return bookingClient.getAllBookingsOfOwner(ownerId, state);
    }


}
