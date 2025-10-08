package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoTest {

    private final JacksonTester<ItemDto> json;

    @Test
    void itemDtoSerializationTest() throws Exception {

        ItemDto itemDto = new ItemDto(1, "Test", "Test", "true", 1, 2);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.available").isEqualTo("true");
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(itemDto.getUserId());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId());
    }

    @Test
    void itemDtoWithNullValues() throws Exception {

        ItemDto dto = new ItemDto(0, null, null, null, 0, null);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.description").isNull();
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.userId").isEqualTo(0);
        assertThat(result).extractingJsonPathValue("$.requestId").isNull();
    }

    @Test
    void itemDtoWithEmptyName() throws Exception {
        ItemDto dto = new ItemDto(1, "", "Test", "true", 1, 2);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathValue("$.name").isEqualTo("");
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo("Test");
    }
}