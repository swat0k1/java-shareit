package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    @MockBean
    private final BookingService bookingService;

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private BookingCreateDto bookingCreateDto;
    private BookingDto bookingDto;
    private User user;
    private Item item;

    private static final String HEADER = "X-Sharer-User-Id";
    private static final String URL = "/bookings";

    @BeforeEach
    void setUp() {
        user = new User(1, "Booker", "booker@booker.ru");
        item = new Item(1, "Test", "Test", true, null, null);

        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        bookingDto = new BookingDto(1, time, time.plusSeconds(1), item, user, BookingStatus.APPROVED);
    }

    @Test
    void createBookingCorrect() throws Exception {
        bookingCreateDto = new BookingCreateDto(0, bookingDto.getStart(), bookingDto.getEnd(), item.getId(), bookingDto.getStatus());
        when(bookingService.create(user.getId(), bookingCreateDto)).thenReturn(bookingDto);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.booker.name", is(user.getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void createBookingIncorrect() throws Exception {
        bookingCreateDto = new BookingCreateDto(6379862, null, null, 0, BookingStatus.WAITING);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(bookingCreateDto).replace("6379862", "6379862000"))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatusCorrect() throws Exception {
        when(bookingService.updateStatus(user.getId(), bookingDto.getId(), true)).thenReturn(bookingDto);

        mvc.perform(patch(URL + "/" + bookingDto.getId())
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId())))
                .andExpect(jsonPath("$.item.name", is(item.getName())));
    }

    @Test
    void updateStatusIncorrect() throws Exception {
        when(bookingService.updateStatus(user.getId(), bookingDto.getId(), true))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mvc.perform(patch(URL + bookingDto.getId())
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdCorrect() throws Exception {
        when(bookingService.findById(user.getId(), bookingDto.getId())).thenReturn(bookingDto);

        mvc.perform(get(URL + "/" + bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId())))
                .andExpect(jsonPath("$.item.name", is(item.getName())));
    }

    @Test
    void findByIdIncorrect() throws Exception {
        when(bookingService.findById(user.getId(), bookingDto.getId()))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mvc.perform(get(URL + bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllBookingsOfBooker() throws Exception {
        when(bookingService.findAllUsersBookings(user.getId(), BookingRequestState.FUTURE)).thenReturn(List.of(bookingDto));

        mvc.perform(get(URL)
                        .param("state", BookingRequestState.FUTURE.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId())))
                .andExpect(jsonPath("$[0].item.name", is(item.getName())));
    }

    @Test
    void findAllBookingsOfOwner() throws Exception {
        when(bookingService.findAllBookingsOfOwner(user.getId(), BookingRequestState.FUTURE)).thenReturn(List.of(bookingDto));

        mvc.perform(get(URL + "/owner")
                        .param("state", BookingRequestState.FUTURE.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId())))
                .andExpect(jsonPath("$[0].item.name", is(item.getName())));
    }

}
