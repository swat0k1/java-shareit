package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoTest {

    private final JacksonTester<ItemRequestDto> itemRequestDtoJacksonTester;

    @Test
    void itemRequestDtoSerializationTest() throws Exception {
        ItemRequestDto itemRequestDto = createTestDto(1, "Test", 1);

        JsonContent<ItemRequestDto> result = itemRequestDtoJacksonTester.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDto.getCreated().toString());
    }

    @Test
    void itemRequestDtoSerializationWithNullValuesTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(0);
        itemRequestDto.setDescription(null);
        itemRequestDto.setRequestorId(null);
        itemRequestDto.setCreated(null);

        JsonContent<ItemRequestDto> result = itemRequestDtoJacksonTester.write(itemRequestDto);

        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathValue("$.description").isNull();
        assertThat(result).extractingJsonPathValue("$.requestorId").isNull();
        assertThat(result).extractingJsonPathValue("$.created").isNull();
    }

    @Test
    void itemRequestDtoSerializationWithFutureDateTest() throws Exception {
        LocalDateTime futureDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ItemRequestDto itemRequestDto = createTestDto(2, "Test", 2);
        itemRequestDto.setCreated(futureDate);

        JsonContent<ItemRequestDto> result = itemRequestDtoJacksonTester.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(futureDate.truncatedTo(ChronoUnit.SECONDS).toString());
    }

    private ItemRequestDto createTestDto(Integer id, String description, Integer requestorId) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(id);
        itemRequestDto.setDescription(description);
        itemRequestDto.setRequestorId(requestorId);
        itemRequestDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return itemRequestDto;
    }
}