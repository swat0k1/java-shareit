package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String REQUEST_ENDPOINT = "/requests";
    private static final String REQUEST_URL = "${shareit-server.url}";

    public ItemRequestClient(@Value(REQUEST_URL) String requestUrl, RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder.uriTemplateHandler(new DefaultUriBuilderFactory(requestUrl + REQUEST_ENDPOINT))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory()).build());
    }

    public ResponseEntity<Object> createItemRequest(Integer requestorId, ItemRequestCreateDto itemRequestCreateDto) {
        return postRequest("", requestorId, null, itemRequestCreateDto);
    }

    public ResponseEntity<Object> getUserRequests(Integer requestorId) {
        return getRequest("", requestorId, null);
    }

    public ResponseEntity<Object> getRequestsOfAnotherUser(Integer anotherUserId) {
        return getRequest("/all", anotherUserId, null);
    }

    public ResponseEntity<Object> getItemRequestById(Integer requestId) {
        return getRequest("/" + requestId, null, null);
    }

}
