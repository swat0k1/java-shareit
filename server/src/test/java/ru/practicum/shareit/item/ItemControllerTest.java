package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.UpdateItem;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @MockBean
    private final ItemService itemService;

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private ItemDto itemDto;
    private CommentDto commentDto;
    ItemDateDto itemDateDto;

    private static final String URL = "/items";
    private static final String HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1, "Test", "Test", "true", 1, 11);
        commentDto = new CommentDto(1, "Test", 1, "Author", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        itemDateDto = new ItemDateDto();
        itemDateDto.setId(itemDto.getId());
        itemDateDto.setName(itemDto.getName());
        itemDateDto.setDescription(itemDto.getDescription());
        itemDateDto.setAvailable(Boolean.valueOf(itemDto.getAvailable()));
        itemDateDto.setUserId(itemDto.getUserId());
        itemDateDto.setLastBooking(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusDays(1));
        itemDateDto.setNextBooking(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1));
        itemDateDto.setComments(List.of(commentDto));
        itemDateDto.setRequestId(itemDto.getRequestId());

    }

    @Test
    void createItemSuccessfully() throws Exception {
        when(itemService.create(itemDto.getUserId(), itemDto)).thenReturn(itemDto);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemDto.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.userId", is(itemDto.getUserId()), Integer.class));
    }

    @Test
    void createItemWithInvalidData() throws Exception {
        itemDto.setName(null);
        when(itemService.create(itemDto.getUserId(), itemDto)).thenThrow(new WrongDataException("Предмет не может быть пустым"));

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemDto.getUserId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemSuccessfully() throws Exception {
        UpdateItem updateItem = new UpdateItem();
        updateItem.setName("Test");
        updateItem.setDescription("Test");
        updateItem.setAvailable("true");
        when(itemService.update(itemDto.getUserId(), itemDto.getId(), updateItem)).thenReturn(itemDto);

        mvc.perform(patch(URL + "/" + itemDto.getId())
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemDto.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.userId", is(itemDto.getUserId()), Integer.class));
    }

    @Test
    void findItemByIdSuccessfully() throws Exception {
        when(itemService.getItemById(itemDto.getId())).thenReturn(itemDateDto);

        mvc.perform(get(URL + "/" + itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDateDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDateDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDateDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDateDto.getAvailable())))
                .andExpect(jsonPath("$.userId", is(itemDateDto.getUserId()), Integer.class));
    }

    @Test
    void findItemByIdThrowsException() throws Exception {
        when(itemService.getItemById(itemDto.getId())).thenThrow(new NotFoundException("Предмет не найден"));

        mvc.perform(get(URL + "/" + itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllUserItemsSuccessfully() throws Exception {
        when(itemService.getAllUsersItems(itemDto.getUserId())).thenReturn(List.of(itemDateDto));

        mvc.perform(get(URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemDto.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())));
    }

    @Test
    void findByQueryTextSuccessfully() throws Exception {
        when(itemService.getItemsByQuery("item")).thenReturn(List.of(itemDto));

        mvc.perform(get(URL + "/search")
                        .param("text", "item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemDto.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())));
    }

    @Test
    void deleteItemSuccessfully() throws Exception {
        when(itemService.deleteItem(itemDto.getUserId(), itemDto.getId())).thenReturn(itemDto);

        mvc.perform(delete(URL + "/" + itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemDto.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    void addCommentSuccessfully() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText("Text");
        when(itemService.addComment(itemDto.getUserId(), itemDto.getId(), createCommentDto)).thenReturn(commentDto);

        mvc.perform(post(URL + "/" + itemDto.getId() + "/comment")
                        .content(mapper.writeValueAsString(createCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemDto.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    void addCommentWithInvalidItem() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText("Test");
        when(itemService.addComment(itemDto.getUserId(), 999, createCommentDto)).thenThrow(new NotFoundException("Предмет не найден"));

        mvc.perform(post(URL + "/999/comment")
                        .content(mapper.writeValueAsString(createCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemDto.getUserId()))
                .andExpect(status().isNotFound());
    }
}