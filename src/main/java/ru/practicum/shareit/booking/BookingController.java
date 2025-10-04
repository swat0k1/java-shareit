package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequestState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader(REQUEST_HEADER) int userId, @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        return bookingService.create(userId, bookingCreateDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(REQUEST_HEADER) int userId, @PathVariable(name = "bookingId") int bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllUsersBookings(@RequestHeader(REQUEST_HEADER) int userId,
                                                 @RequestParam(name = "state", required = false, defaultValue = "ALL")BookingRequestState bookingRequestState) {
        return bookingService.findAllUsersBookings(userId, bookingRequestState);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllOwnersBookings(@RequestHeader(REQUEST_HEADER) int ownerId,
                                                  @RequestParam(name = "state", required = false, defaultValue = "ALL") BookingRequestState bookingRequestState) {
        return bookingService.findAllBookingsOfOwner(ownerId, bookingRequestState);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader(REQUEST_HEADER) int userId, @PathVariable(name = "bookingId") int bookingId,
                                   @RequestParam(name = "approved") Boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }

}
