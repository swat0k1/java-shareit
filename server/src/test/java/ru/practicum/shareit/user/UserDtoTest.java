package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoTest {
    private final JacksonTester<UserDto> json;

    @Test
    void userDtoSerialization() throws Exception {
        UserDto userDto = new UserDto(1, "Test@Test.ru", "Test");
        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Test@Test.ru");
    }

    @Test
    void userDtoDeserialization() throws Exception {
        String jsonInput = "{\"id\":2,\"name\":\"Test\",\"email\":\"Test@Test.ru\"}";
        UserDto result = json.parse(jsonInput).getObject();

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getName()).isEqualTo("Test");
        assertThat(result.getEmail()).isEqualTo("Test@Test.ru");
    }

    @Test
    void userDtoWithNullValues() throws Exception {
        UserDto userDto = new UserDto(0, null, null);
        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }

    @Test
    void userDtoEquality() {
        UserDto userDto1 = new UserDto(1, "Test", "Test@Test.ru");
        UserDto userDto2 = new UserDto(1, "Test", "Test@Test.ru");

        assertThat(userDto1).isEqualTo(userDto2);
    }
}