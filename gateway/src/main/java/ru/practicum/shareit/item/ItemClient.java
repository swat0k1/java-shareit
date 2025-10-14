package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.UpdateItem;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String REQUEST_ENDPOINT = "/items";
    private static final String REQUEST_URL = "${shareit-server.url}";

    public ItemClient(@Value(REQUEST_URL) String requestUrl, RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder.uriTemplateHandler(new DefaultUriBuilderFactory(requestUrl + REQUEST_ENDPOINT))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory()).build());
    }

    public ResponseEntity<Object> createItem(Integer userId, ItemDto itemDto) {
        return postRequest("", userId, null, itemDto);
    }

    public ResponseEntity<Object> getItemById(Integer itemId) {
        return getRequest("/" + itemId, null, null);
    }

    public ResponseEntity<Object> getAllUsersItems(Integer userId) {
        return getRequest("", userId, null);
    }

    public ResponseEntity<Object> updateItem(Integer userId, Integer itemId, UpdateItem updateItem) {
        return patchRequest("/" + itemId, userId, null, updateItem);
    }

    public ResponseEntity<Object> getByQuery(Integer userId, String query) {
        Map<String, Object> param = Map.of("query", query);
        return getRequest("/search?text={query}", userId, param);
    }

    public ResponseEntity<Object> deleteItem(Integer userId, Integer itemId) {
        return deleteRequest("/" + itemId, userId, null);
    }

    public ResponseEntity<Object> createComment(Integer bookerId, Integer itemId, CreateCommentDto createCommentDto) {
        return postRequest("/" + itemId + "/comment", bookerId, null, createCommentDto);
    }

}
