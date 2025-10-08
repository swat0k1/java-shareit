package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.UpdateItem;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService requestService;
    private final BookingService bookingService;

    private UserDto owner;
    private ItemDto item;
    private UpdateItem updateItem;

    @BeforeEach
    void setUp() {
        owner = new UserDto(0, "Test@Test.ru", "Test");
        item = new ItemDto(0, "Test", "Test", "true", 0, null);
        owner = userService.createUser(owner);
        item = itemService.create(owner.getId(), item);
    }

    @Test
    void create() {
        ItemDto result = itemService.create(owner.getId(), item);
        assertThat(result, notNullValue());
        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.getUserId(), equalTo(owner.getId()));
        assertThat(result.getRequestId(), nullValue());
    }

    @Test
    void createByRequest() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("Test");
        ItemRequestDto requestDto = requestService.createItemRequest(owner.getId(), itemRequestCreateDto);
        item.setRequestId(requestDto.getId());
        ItemDto result = itemService.create(owner.getId(), item);
        assertThat(result, notNullValue());
        assertThat(result.getId(), notNullValue());
        assertThat(result.getRequestId(), equalTo(requestDto.getId()));
    }

    @Test
    void createByNoExistUser() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.create(999, item));
        assertThat(exception.getMessage(), equalTo("Not found exception: Пользователь не найден"));
    }

    @Test
    void createByNoExistRequest() {
        item.setRequestId(999);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.create(owner.getId(), item));
        assertThat(exception.getMessage(), equalTo("Not found exception: Запрос не найден"));
    }

    @Test
    void update() {
        updateItem = new UpdateItem();
        updateItem.setName("Test");
        updateItem.setDescription("Test");
        updateItem.setAvailable("false");
        ItemDto result = itemService.update(item.getUserId(), item.getId(), updateItem);
        assertThat(result.getName(), equalTo(updateItem.getName()));
        assertThat(result.getDescription(), equalTo(updateItem.getDescription()));
        assertThat(result.getAvailable(), equalTo(updateItem.getAvailable()));
    }

    @Test
    void updateByNoUser() {
        updateItem = new UpdateItem();
        updateItem.setName("Test");
        PermissionException exception = assertThrows(PermissionException.class, () -> itemService.update(999, item.getId(), updateItem));
        assertThat(exception.getMessage(), equalTo("Permission exception: Не достаточно прав для обновления объекта или пользователь не был найден"));
    }

    @Test
    void updateName() {
        updateItem = new UpdateItem();
        updateItem.setName("Test");
        ItemDto result = itemService.update(item.getUserId(), item.getId(), updateItem);
        assertThat(result.getName(), equalTo(updateItem.getName()));
    }

    @Test
    void findItemById() {
        ItemDateDto result = itemService.getItemById(item.getId());
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getComments(), empty());
    }

    @Test
    void findAllUserItems() {
        List<ItemDateDto> result = itemService.getAllUsersItems(owner.getId());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getName(), equalTo(item.getName()));
    }

    @Test
    void findByQueryTextName() {
        List<ItemDto> result = itemService.getItemsByQuery(item.getName());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findByQueryTextDescription() {
        List<ItemDto> result = itemService.getItemsByQuery(item.getDescription());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findByEmptyQueryText() {
        List<ItemDto> result = itemService.getItemsByQuery("");
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void delete() {
        itemService.deleteItem(owner.getId(), item.getId());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.getItemById(item.getId()));
        assertThat(exception.getMessage(), equalTo("Not found exception: Предмет не найден"));
    }

    @Test
    void deleteByNoUser() {
        PermissionException exception = assertThrows(PermissionException.class, () -> itemService.deleteItem(999, item.getId()));
        assertThat(exception.getMessage(), equalTo("Permission exception: Не достаточно прав для удаления объекта"));
    }

    @Test
    void addComment() throws InterruptedException {
        UserDto booker = new UserDto(0, "Test@Test.com", "Test");
        booker = userService.createUser(booker);
        BookingCreateDto createBookingDto = new BookingCreateDto(0, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item.getId(), BookingStatus.APPROVED);
        bookingService.create(booker.getId(), createBookingDto);
        TimeUnit.SECONDS.sleep(3);
        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText("Test");
        CommentDto commentDto = itemService.addComment(booker.getId(), item.getId(), createCommentDto);
        assertThat(commentDto.getText(), equalTo(createCommentDto.getText()));
    }

    @Test
    void addCommentByNotBooking() throws InterruptedException {
        UserDto booker = new UserDto(0, "Test22@Test.ru", "Test");
        UserDto booker1 = userService.createUser(booker);
        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText("Test");
        WrongDataException exception = assertThrows(WrongDataException.class, () -> itemService.addComment(booker1.getId(), item.getId(), createCommentDto));
        assertThat(exception.getMessage(), equalTo("Пользователь не бронировал данный предмет"));
    }

    @Test
    void updateItemWithInvalidData() {
        updateItem = new UpdateItem();
        PermissionException exception = assertThrows(PermissionException.class, () -> itemService.update(-5, item.getId(), updateItem));
        assertThat(exception.getMessage(), equalTo("Permission exception: Не достаточно прав для обновления объекта или пользователь не был найден"));
    }

    @Test
    void findItemByNonExistentId() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.getItemById(999));
        assertThat(exception.getMessage(), equalTo("Not found exception: Предмет не найден"));
    }

    @Test
    void findAllUserItemsNoItems() {
        UserDto secondUser = new UserDto(0, "TestTest@TestTest.ru", "TestTest");
        userService.createUser(secondUser);
        List<ItemDateDto> result = itemService.getAllUsersItems(secondUser.getId());
        assertThat(result, empty());
    }
}