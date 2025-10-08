package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;
import ru.practicum.shareit.request.model.ItemRequestData;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestWithItemsDtoTest {

    private final JacksonTester<ItemRequestItemsDto> itemRequestItemsDtoTester;

    @Test
    void itemRequestWithItemsDtoTest() throws Exception {
        ItemRequestData dataOfItem = new ItemRequestData();
        dataOfItem.setItemId(2);
        dataOfItem.setName("Test");
        dataOfItem.setOwnerId(2);

        ItemRequestItemsDto withItemsDto = new ItemRequestItemsDto();
        withItemsDto.setId(1);
        withItemsDto.setDescription("Test");
        withItemsDto.setRequestorId(1);
        withItemsDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        withItemsDto.setItems(List.of(dataOfItem));

        JsonContent<ItemRequestItemsDto> result = itemRequestItemsDtoTester.write(withItemsDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(withItemsDto.getCreated().truncatedTo(ChronoUnit.SECONDS).toString());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Test");
    }

    @Test
    void itemRequestWithMultipleItemsDtoTest() throws Exception {
        ItemRequestData item1 = new ItemRequestData();
        item1.setItemId(1);
        item1.setName("Test");
        item1.setOwnerId(1);

        ItemRequestData item2 = new ItemRequestData();
        item2.setItemId(2);
        item2.setName("Test");
        item2.setOwnerId(2);

        List<ItemRequestData> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        ItemRequestItemsDto withItemsDto = new ItemRequestItemsDto();
        withItemsDto.setId(2);
        withItemsDto.setDescription("Test");
        withItemsDto.setRequestorId(2);
        withItemsDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        withItemsDto.setItems(items);

        JsonContent<ItemRequestItemsDto> result = itemRequestItemsDtoTester.write(withItemsDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo("Test");
    }

    @Test
    void itemRequestWithEmptyItemsTest() throws Exception {
        ItemRequestItemsDto withItemsDto = new ItemRequestItemsDto();
        withItemsDto.setId(3);
        withItemsDto.setDescription("Test");
        withItemsDto.setRequestorId(3);
        withItemsDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        withItemsDto.setItems(new ArrayList<>());

        JsonContent<ItemRequestItemsDto> result = itemRequestItemsDtoTester.write(withItemsDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(3);
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }
}