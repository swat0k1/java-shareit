package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestService requestService;
    private final UserService userService;
    private final ItemService itemService;

    UserDto requestor;
    UserDto owner;
    ItemRequestCreateDto itemRequestCreateDto;
    ItemRequestDto itemRequestDto;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        requestor = userService.createUser(new UserDto(0, "Test@Test.ru", "Test"));
        owner = userService.createUser(new UserDto(0, "Test1@Test.ru", "Test1"));

        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("Test");
        itemRequestDto = requestService.createItemRequest(requestor.getId(), itemRequestCreateDto);

        itemDto = itemService.create(owner.getId(), new ItemDto(0, "Item", "Test", "true", 0, itemRequestDto.getId()));
    }

    @Test
    void create() {
        ItemRequestDto result = requestService.createItemRequest(requestor.getId(), itemRequestCreateDto);
        assertThat(result, allOf(
                notNullValue(),
                hasProperty("id"),
                hasProperty("description", equalTo(itemRequestCreateDto.getDescription())),
                hasProperty("requestorId", equalTo(requestor.getId())),
                hasProperty("created", notNullValue())
        ));
    }

    @Test
    void createByNotExistUser() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> requestService.createItemRequest(999, itemRequestCreateDto));
        assertThat(exception.getMessage(), equalTo("Not found exception: User not found: 999"));
    }

    @Test
    void findUserRequests() {
        List<ItemRequestItemsDto> result = requestService.findUserItemRequests(requestor.getId());
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(result.get(0).getRequestorId(), equalTo(requestor.getId()));
    }

    @Test
    void findRequestsOfOtherUsers() {
        List<ItemRequestDto> result = requestService.findRequestsOfAnotherUser(owner.getId());
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(result.get(0).getRequestorId(), not(equalTo(owner.getId())));
    }

    @Test
    void findItemRequestById() {
        ItemRequestItemsDto result = requestService.findItemRequestById(itemRequestDto.getId());
        assertThat(result, allOf(
                notNullValue(),
                hasProperty("id", equalTo(itemRequestDto.getId())),
                hasProperty("description", equalTo(itemRequestDto.getDescription())),
                hasProperty("requestorId", equalTo(itemRequestDto.getRequestorId())),
                hasProperty("created", equalTo(itemRequestDto.getCreated())),
                hasProperty("items", not(empty()))
        ));
    }

    @Test
    void findItemRequestByNotExistId() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> requestService.findItemRequestById(999));
        assertThat(exception.getMessage(), equalTo("Not found exception: Item request not found: 999"));
    }

    @Test
    void findRequestsReturnsEmptyListWhenNoRequests() {
        UserDto newUser = userService.createUser(new UserDto(0, "Test", "Test@Test.ru"));
        List<ItemRequestItemsDto> result = requestService.findUserItemRequests(newUser.getId());
        assertThat(result, is(empty()));
    }

    @Test
    void findItemRequestByIdWithMultipleItems() {
        ItemRequestCreateDto anotherDto = new ItemRequestCreateDto();
        anotherDto.setDescription("Test");
        ItemRequestDto anotherRequestDto = requestService.createItemRequest(requestor.getId(), anotherDto);

        ItemDto anotherItem = new ItemDto(0, "Test", "Test", "true", 0, anotherRequestDto.getId());
        itemService.create(owner.getId(), anotherItem);

        ItemRequestItemsDto result = requestService.findItemRequestById(anotherRequestDto.getId());
        assertThat(result.getItems(), hasSize(1));
        assertThat(result.getItems().get(0).getName(), equalTo(anotherItem.getName()));
    }
}