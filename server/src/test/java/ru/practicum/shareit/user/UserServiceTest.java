package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UpdateUser;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(0, "Test@Test.ru", "Test");
        userDto = userService.createUser(userDto);
    }

    @Test
    void findUserById() {
        UserDto result = userService.getUserById(userDto.getId());
        assertThat(result.getId(), equalTo(userDto.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void findUserByNonExistentId() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(999));
        assertThat("Not found exception: Пользователь не найден", equalTo(exception.getMessage()));
    }

    @Test
    void findUsers() {
        Collection<UserDto> result = userService.getUsers();
        assertThat(result, notNullValue());
        assertThat(result, not(empty()));
    }

    @Test
    void create() {
        UserDto result = userService.createUser(new UserDto(0, "Test@Test1.ru", "Test"));
        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo("Test"));
        assertThat(result.getEmail(), equalTo("Test@Test1.ru"));
    }

    @Test
    void createWithDuplicateEmail() {
        assertThrows(UserDataException.class,
                () -> userService.createUser(userDto));
    }

    @Test
    void updateUser() {
        UpdateUser request = new UpdateUser("Test@Test1.ru", "Test");
        UserDto result = userService.updateUser(userDto.getId(), request);
        assertThat(result.getId(), equalTo(userDto.getId()));
        assertThat(result.getName(), equalTo(request.getName()));
        assertThat(result.getEmail(), equalTo(request.getEmail()));
    }

    @Test
    void updateNameOnly() {
        UpdateUser request = new UpdateUser(null, "Test");
        UserDto result = userService.updateUser(userDto.getId(), request);
        assertThat(result.getId(), equalTo(userDto.getId()));
        assertThat(result.getName(), equalTo(request.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateEmailOnly() {
        UpdateUser request = new UpdateUser("Test@Test1.ru", null);
        UserDto result = userService.updateUser(userDto.getId(), request);
        assertThat(result.getId(), equalTo(userDto.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(request.getEmail()));
    }

    @Test
    void deleteUser() {
        userService.deleteUser(userDto.getId());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(userDto.getId()));
        assertThat("Not found exception: Пользователь не найден", equalTo(exception.getMessage()));
    }

    @Test
    void createUserWithEmptyEmail() {
        UserDto dtoWithEmptyEmail = new UserDto(0, null, "Test");
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.createUser(dtoWithEmptyEmail));
    }

    @Test
    void updateToDuplicateEmail() {
        userService.createUser(new UserDto(0, "Test@Test33.ru", "test"));
        UpdateUser request = new UpdateUser("Test@Test33.ru", null);
        assertThrows(UserDataException.class,
                () -> userService.updateUser(userDto.getId(), request));
    }

    @Test
    void updateUserNonExistId() {
        UpdateUser request = new UpdateUser("Test@Test.ru", "Test");
        UserDataException exception = assertThrows(UserDataException.class, () -> userService.updateUser(999, request));
        assertThat("User data exception: Пользователь с данным email уже существует", equalTo(exception.getMessage()));
    }
}