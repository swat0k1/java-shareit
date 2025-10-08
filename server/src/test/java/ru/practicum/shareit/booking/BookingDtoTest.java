package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoTest {

    private final JacksonTester<BookingDto> json;

    @Test
    void bookingDtoSerializationTest() throws Exception {
        User booker = new User(1, "Booker", "booker@booker.ru");
        Item item = new Item(1, "Test", "Test", true, null, null);

        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime end = start.plusSeconds(1);
        BookingDto dto = new BookingDto(1, start, end, item, booker, BookingStatus.APPROVED);

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id", dto.getId());
        assertThat(result).hasJsonPathStringValue("$.start", start.toString());
        assertThat(result).hasJsonPathStringValue("$.end", end.toString());
        assertThat(result).hasJsonPathStringValue("$.item.name", item.getName());
        assertThat(result).hasJsonPathStringValue("$.booker.name", booker.getName());
        assertThat(result).hasJsonPathStringValue("$.status", dto.getStatus().toString());
    }

    @Test
    void bookingDtoWithNullValuesTest() throws Exception {
        BookingDto dto = new BookingDto(0, null, null, null, null, null);

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).hasJsonPathValue(".id", null);
        assertThat(result).hasJsonPathValue(".start", null);
        assertThat(result).hasJsonPathValue(".end", null);
        assertThat(result).hasJsonPathValue(".item", null);
        assertThat(result).hasJsonPathValue(".booker", null);
        assertThat(result).hasJsonPathValue(".status", null);
    }

    @Test
    void bookingDtoWithDifferentStatusesTest() throws Exception {
        User booker = new User(2, "Another Booker", "another@another.com");
        Item item = new Item(2, "Another Item", "Another description", true, null, null);

        for (BookingStatus status : BookingStatus.values()) {
            LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            LocalDateTime end = start.plusSeconds(1);
            BookingDto dto = new BookingDto(3, start, end, item, booker, status);

            JsonContent<BookingDto> result = json.write(dto);

            assertThat(result).hasJsonPathNumberValue("$.id", dto.getId());
            assertThat(result).hasJsonPathStringValue("$.status", status.toString());
        }
    }
}