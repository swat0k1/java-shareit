package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;


@Service
public class BookingClient extends BaseClient {

    private static final String REQUEST_ENDPOINT = "/bookings";
    private static final String REQUEST_URL = "${shareit-server.url}";

    public BookingClient(@Value(REQUEST_URL) String requestUrl, RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder.uriTemplateHandler(new DefaultUriBuilderFactory(requestUrl + REQUEST_ENDPOINT))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory()).build());
    }

    public ResponseEntity<Object> createBooking(Integer bookerId, BookingCreateDto bookingCreateDto) {
        return postRequest("", bookerId, null, bookingCreateDto);
    }

    public ResponseEntity<Object> getBookingById(Integer userId, Integer bookingId) {
        return getRequest("/" + bookingId, userId, null);
    }

    public ResponseEntity<Object> getAllBookingsOfBooker(Integer userId, BookingRequestState bookingRequestState) {
        Map<String, Object> param = Map.of("state", bookingRequestState);
        return getRequest("", userId, param);
    }

    public ResponseEntity<Object> getAllBookingsOfOwner(Integer ownerId, BookingRequestState bookingRequestState) {
        Map<String, Object> param = Map.of("state", bookingRequestState);
        return getRequest("/owner", ownerId, param);
    }

    public ResponseEntity<Object> updateBookingStatus(Integer userId, Integer bookingId, Boolean approved) {
        Map<String, Object> param = Map.of("approved", approved);
        return patchRequest("/" + bookingId + "?approved={approved}", userId, param, null);
    }

}
