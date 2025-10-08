package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;
import ru.practicum.shareit.request.model.ItemRequestData;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @MockBean
    private final ItemRequestService requestService;

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private ItemRequestDto itemRequestDto;
    private ItemRequestItemsDto itemRequestItemsDto;
    private ItemRequestData itemRequestData;

    private static final String URL = "/requests";
    private static final String HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("Test");
        itemRequestDto.setRequestorId(1);
        itemRequestDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        itemRequestData = new ItemRequestData();
        itemRequestData.setItemId(2);
        itemRequestData.setName("Test");
        itemRequestData.setOwnerId(2);

        itemRequestItemsDto = new ItemRequestItemsDto();
        itemRequestItemsDto.setId(1);
        itemRequestItemsDto.setDescription("Test");
        itemRequestItemsDto.setRequestorId(1);
        itemRequestItemsDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        itemRequestItemsDto.setItems(List.of(itemRequestData));
    }

    
    @Test
    void create() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Test");
        when(requestService.createItemRequest(itemRequestDto.getRequestorId(), createDto)).thenReturn(itemRequestDto);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemRequestDto.getRequestorId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(itemRequestDto.getRequestorId()), Integer.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().truncatedTo(ChronoUnit.SECONDS).toString())));
    }

    @Test
    void findUserRequests() throws Exception {
        when(requestService.findUserItemRequests(itemRequestItemsDto.getRequestorId())).thenReturn(List.of(itemRequestItemsDto));

        mvc.perform(get(URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemRequestItemsDto.getRequestorId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(itemRequestItemsDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestItemsDto.getDescription())))
                .andExpect(jsonPath("$[0].requestorId", is(itemRequestItemsDto.getRequestorId()), Integer.class));
    }

    @Test
    void findRequestsOfOtherUsers() throws Exception {
        when(requestService.findRequestsOfAnotherUser(itemRequestDto.getRequestorId())).thenReturn(new ArrayList<>());

        mvc.perform(get(URL + "/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemRequestDto.getRequestorId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findItemRequestById() throws Exception {
        when(requestService.findItemRequestById(itemRequestItemsDto.getId())).thenReturn(itemRequestItemsDto);

        mvc.perform(get(URL + "/" + itemRequestItemsDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestItemsDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestItemsDto.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(itemRequestItemsDto.getRequestorId()), Integer.class))
                .andExpect(jsonPath("$.created", is(itemRequestItemsDto.getCreated().truncatedTo(ChronoUnit.SECONDS).toString())))
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void createRequestWithoutDescription() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        when(requestService.createItemRequest(itemRequestDto.getRequestorId(), itemRequestCreateDto))
                .thenThrow(new WrongDataException(""));

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, itemRequestDto.getRequestorId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findRequestsByInvalidUserId() throws Exception {
        when(requestService.findUserItemRequests(-1))
                .thenThrow(new WrongDataException(""));

        mvc.perform(get(URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findItemRequestByIdNotFound() throws Exception {
        when(requestService.findItemRequestById(99))
                .thenThrow(new NotFoundException(""));

        mvc.perform(get(URL + "/99")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}