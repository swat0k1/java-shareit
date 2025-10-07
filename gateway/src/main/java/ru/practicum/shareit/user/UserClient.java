package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UpdateUser;

@Service
public class UserClient extends BaseClient {

    private static final String REQUEST_ENDPOINT = "/users";
    private static final String REQUEST_URL = "${shareit-server.url}";

    public UserClient(@Value(REQUEST_URL) String requestUrl, RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder.uriTemplateHandler(new DefaultUriBuilderFactory(requestUrl + REQUEST_ENDPOINT))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory()).build());
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return postRequest("", null, null, userDto);
    }

    public ResponseEntity<Object> getUserById(Integer userId) {
        return getRequest("/" + userId, null, null);
    }

    public ResponseEntity<Object> getUsers() {
        return getRequest("", null, null);
    }

    public ResponseEntity<Object> updateUser(Integer userId, UpdateUser updateUser) {
        return patchRequest("/" + userId, null, null, updateUser);
    }

    public ResponseEntity<Object> deleteUser(Integer userId) {
        return deleteRequest("/" + userId, null,null);
    }

}
