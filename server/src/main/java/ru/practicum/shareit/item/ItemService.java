package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStorageDb;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDates;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentStorageDb;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.item.dto.ItemDateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.UpdateItem;
import ru.practicum.shareit.request.ItemRequestStorageDb;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserStorageDb;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class ItemService {

    private final ItemStorageDb itemStorageDb;
    private final UserStorageDb userStorageDb;
    private final BookingStorageDb bookingStorageDb;
    private final CommentStorageDb commentStorageDb;
    private final ItemRequestStorageDb itemRequestStorageDb;

    public ItemService(ItemStorageDb itemStorageDb, UserStorageDb userStorageDb,
                       BookingStorageDb bookingStorageDb, CommentStorageDb commentStorageDb,
                       ItemRequestStorageDb itemRequestStorageDb) {
        this.itemStorageDb = itemStorageDb;
        this.userStorageDb = userStorageDb;
        this.bookingStorageDb = bookingStorageDb;
        this.commentStorageDb = commentStorageDb;
        this.itemRequestStorageDb = itemRequestStorageDb;
    }

    @Transactional
    public ItemDto create(int id, ItemDto itemDto) {
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestStorageDb.findById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException("Запрос не найден"));
        }
        User user = userStorageDb.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.mapToItem(itemDto, user, itemRequest);
        return ItemMapper.mapToDto(itemStorageDb.save(item));
    }

    @Transactional
    public ItemDto update(int userId, int itemId, UpdateItem updateItem) {
        Item item = itemStorageDb.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));

        if (item.getOwnerUser() == null || (item.getOwnerUser().getId() != userId)) {
            throw new PermissionException("Не достаточно прав для обновления объекта или пользователь не был найден");
        }

        updateUsersFields(updateItem, item);
        return ItemMapper.mapToDto(itemStorageDb.save(item));

    }

    public ItemDateDto getItemById(int id) {
        Item item = itemStorageDb.findById(id).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        BookingDates bookingDates = bookingStorageDb.findBookingDates(id, LocalDateTime.now());
        List<Comment> comments = commentStorageDb.findAllByItemId(id);
        return ItemMapper.mapToItemDateDto(item, bookingDates, CommentMapper.mapToCommentDtos(comments));
    }

    public List<ItemDateDto> getAllUsersItems(int id) {
        List<Item> items = itemStorageDb.findAllByOwnerUserId(id);
        List<BookingDates> bookingDates = bookingStorageDb.findAllBookingsDatesOfUser(id, LocalDateTime.now());
        List<Comment> comments = commentStorageDb.findAllByItemIdIn(items
                .stream()
                .map(Item::getId)
                .toList());
        List<CommentDto> commentDtos = CommentMapper.mapToCommentDtos(comments);
        return ItemMapper.mapToItemDateDto(items, bookingDates, commentDtos);
    }

    public List<ItemDto> getItemsByQuery(String query) {
        if (query.isBlank()) {
            return new ArrayList<ItemDto>();
        }
        List<Item> items = itemStorageDb.findByQueryText(query);
        return ItemMapper.mapToDtos(items);
    }

    @Transactional
    public ItemDto deleteItem(int userId, int itemId) {
        Item item = itemStorageDb.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (item.getOwnerUser().getId() != userId) {
            throw new PermissionException("Не достаточно прав для удаления объекта");
        }
        itemStorageDb.deleteById(itemId);
        return ItemMapper.mapToDto(item);
    }

    @Transactional
    public CommentDto addComment(int userId, int itemId, CreateCommentDto createCommentDto) {
        User user = userStorageDb.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemStorageDb.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        List<Booking> bookings = bookingStorageDb.findAllByBookingUserIdAndItemIdAndBookingStatusAndEndBookingIsBefore(userId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new WrongDataException("Пользователь не бронировал данный предмет");
        }
        Comment comment = CommentMapper.mapToComment(createCommentDto, user, item);
        Comment savedComment = commentStorageDb.save(comment);
        return CommentMapper.mapToCommentDto(savedComment);
    }

    private void updateUsersFields(UpdateItem updateItem, Item item) {

        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        }

        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }

        if (updateItem.getAvailable() != null) {
            item.setAvailable(Boolean.valueOf(updateItem.getAvailable()));
        }

    }

}
