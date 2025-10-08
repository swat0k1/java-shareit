package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestData;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(ItemRequestCreateDto dto, User requestor) {

        LocalDateTime itemRequestCreationDate = LocalDateTime.now();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(itemRequestCreationDate);

        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Integer requestorId = null;
        if (itemRequest.getRequestor() != null) {
            requestorId = itemRequest.getRequestor().getId();
        }
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestorId(requestorId);
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public static ItemRequestItemsDto mapToItemRequestDto(ItemRequest request, List<Item> items) {
        List<ItemRequestData> dataOfItems = items.stream()
                .map(ItemRequestMapper::mapItemToItemRequestData)
                .collect(Collectors.toList());
        return mapRequestDataToItemRequestItemsDto(request, dataOfItems);
    }

    private static ItemRequestItemsDto mapRequestDataToItemRequestItemsDto(ItemRequest itemRequest, List<ItemRequestData> items) {

        ItemRequestItemsDto itemRequestItemsDto = new ItemRequestItemsDto();
        itemRequestItemsDto.setId(itemRequest.getId());
        itemRequestItemsDto.setDescription(itemRequest.getDescription());
        Integer requestorId = null;
        if (itemRequest.getRequestor() != null) {
            requestorId = itemRequest.getRequestor().getId();
        }
        itemRequestItemsDto.setRequestorId(requestorId);
        itemRequestItemsDto.setCreated(itemRequest.getCreated());
        itemRequestItemsDto.setItems(items);

        return itemRequestItemsDto;
    }

    private static ItemRequestData mapItemToItemRequestData(Item item) {

        ItemRequestData itemRequestData = new ItemRequestData();
        itemRequestData.setItemId(item.getId());
        itemRequestData.setName(item.getName());
        itemRequestData.setOwnerId(item.getOwnerUser().getId());

        return itemRequestData;
    }

    public static List<ItemRequestDto> mapToItemRequestDtos(List<ItemRequest> requests) {
        return requests.stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    public static List<ItemRequestItemsDto> mapToItemRequestDtos(List<ItemRequest> requests, List<Item> items) {

        Map<Integer, List<ItemRequestData>> itemsMap = new HashMap<>();

        for (Item item : items) {
            ItemRequestData data = mapItemToItemRequestData(item);
            Integer requestId = item.getItemRequest().getId();

            if (!itemsMap.containsKey(requestId)) {
                itemsMap.put(requestId, new ArrayList<>());
            }
            itemsMap.get(requestId).add(data);
        }

        List<ItemRequestItemsDto> result = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<ItemRequestData> requestItems = itemsMap.get(request.getId());
            ItemRequestItemsDto dto = mapRequestDataToItemRequestItemsDto(request, requestItems);
            result.add(dto);
        }

        return result;
    }
}
