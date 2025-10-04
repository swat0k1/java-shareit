package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.BookingDates;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto mapToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable().toString(),
                item.getOwnerUser());
    }

    public static List<ItemDto> mapToDtos(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public static Item mapToItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                Boolean.valueOf(itemDto.getAvailable()), itemDto.getOwnerUser());
    }

    public static Item mapToItem(ItemDto itemDto, User user) {

        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                Boolean.valueOf(itemDto.getAvailable()), user);

    }

    public static ItemDateDto mapToItemDateDto(Item item, BookingDates dates) {

        LocalDateTime previousBooking = Optional.ofNullable(dates)
                .map(BookingDates::getPreviousBooking)
                .orElse(null);
        LocalDateTime nextBooking = Optional.ofNullable(dates)
                .map(BookingDates::getNextBooking)
                .orElse(null);

        ItemDateDto itemDateDto = new ItemDateDto();
        itemDateDto.setId(item.getId());
        itemDateDto.setName(item.getName());
        itemDateDto.setDescription(item.getDescription());
        itemDateDto.setAvailable(item.getAvailable());
        itemDateDto.setUserId(item.getOwnerUser().getId());
        itemDateDto.setLastBooking(previousBooking);
        itemDateDto.setNextBooking(nextBooking);
        return itemDateDto;

    }

    public static ItemDateDto mapToItemDateDto(Item item, BookingDates dates, List<CommentDto> commentDtos) {

        ItemDateDto itemDateDto = mapToItemDateDto(item, dates);
        itemDateDto.setComments(Optional.ofNullable(commentDtos).orElseGet(ArrayList::new));

        return itemDateDto;

    }

    public static List<ItemDateDto> mapToItemDateDto(List<Item> items, List<BookingDates> bookingDates) {

        Map<Integer, BookingDates> datesMap = bookingDates.stream()
                .collect(Collectors.toMap(BookingDates::getItemId, Function.identity()));

        return items.stream()
                .map(item -> mapToItemDateDto(item, datesMap.get(item.getId())))
                .collect(Collectors.toList());

    }

    public static List<ItemDateDto> mapToItemDateDto(List<Item> items, List<BookingDates> bookingDates,
                                                     List<CommentDto> commentsDto) {
        Map<Integer, List<CommentDto>> commentMap = commentsDto.stream()
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        Map<Integer, BookingDates> datesMap = bookingDates.stream()
                .collect(Collectors.toMap(BookingDates::getItemId, Function.identity()));

        return items.stream()
                .map(item -> mapToItemDateDto(item, datesMap.get(item.getId()), commentMap.get(item.getId())))
                .collect(Collectors.toList());
    }

}
