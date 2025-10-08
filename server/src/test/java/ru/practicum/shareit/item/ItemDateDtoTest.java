package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDateDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class ItemDateDtoTest {

    private final JacksonTester<ItemDateDto> itemDateDtoTester;

    @Test
    void itemWithDateDtoTest() throws Exception {
        CommentDto commentDto = new CommentDto(1, "Test", 1, "Test",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        ItemDateDto withDateDto = new ItemDateDto();
        withDateDto.setId(1);
        withDateDto.setName("Test");
        withDateDto.setDescription("Test");
        withDateDto.setAvailable(false);
        withDateDto.setUserId(1);
        withDateDto.setLastBooking(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusDays(1));
        withDateDto.setNextBooking(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1));
        withDateDto.setComments(List.of(commentDto));
        withDateDto.setRequestId(2);

        JsonContent<ItemDateDto> result = itemDateDtoTester.write(withDateDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(withDateDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(withDateDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(withDateDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(withDateDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(withDateDto.getUserId());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking")
                .isEqualTo(withDateDto.getLastBooking().truncatedTo(ChronoUnit.SECONDS).toString());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking")
                .isEqualTo(withDateDto.getNextBooking().truncatedTo(ChronoUnit.SECONDS).toString());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(withDateDto.getRequestId());
    }

    @Test
    void itemWithDateDtoEmptyCommentsTest() throws Exception {
        ItemDateDto withDateDto = new ItemDateDto();
        withDateDto.setId(2);
        withDateDto.setName("Test");
        withDateDto.setDescription("Test");
        withDateDto.setAvailable(true);
        withDateDto.setUserId(3);
        withDateDto.setLastBooking(LocalDateTime.now().minusDays(2).truncatedTo(ChronoUnit.SECONDS));
        withDateDto.setNextBooking(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS));
        withDateDto.setComments(List.of());
        withDateDto.setRequestId(null);

        JsonContent<ItemDateDto> result = itemDateDtoTester.write(withDateDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(withDateDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(withDateDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(withDateDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(withDateDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(withDateDto.getUserId());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking")
                .isEqualTo(withDateDto.getLastBooking().toString());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking")
                .isEqualTo(withDateDto.getNextBooking().toString());
        assertThat(result).extractingJsonPathValue("$.comments").isEqualTo(List.of());
        assertThat(result).extractingJsonPathValue("$.requestId").isEqualTo(null);
    }

    @Test
    void itemWithDateDtoNullValuesTest() throws Exception {
        ItemDateDto withDateDto = new ItemDateDto();
        withDateDto.setId(0);
        withDateDto.setName(null);
        withDateDto.setDescription(null);
        withDateDto.setAvailable(null);
        withDateDto.setUserId(0);
        withDateDto.setLastBooking(null);
        withDateDto.setNextBooking(null);
        withDateDto.setComments(null);
        withDateDto.setRequestId(null);

        JsonContent<ItemDateDto> result = itemDateDtoTester.write(withDateDto);

        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.userId").isEqualTo(0);
        assertThat(result).extractingJsonPathValue("$.lastBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.comments").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.requestId").isEqualTo(null);
    }
}