package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {

    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> getRequest(String path, Integer userId,
                                                @Nullable Map<String, Object> parameters) {
        return createSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected <T> ResponseEntity<Object> postRequest(String path, Integer userId,
                                                     @Nullable Map<String, Object> parameters, T body) {
        return createSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> putRequest(String path, Integer userId,
                                                    @Nullable Map<String, Object> parameters, T body) {
        return createSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> patchRequest(String path, Integer userId,
                                                      @Nullable Map<String, Object> parameters, T body) {
        return createSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> deleteRequest(String path, Integer userId,
                                                   @Nullable Map<String, Object> parameters) {
        return createSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    private <T> ResponseEntity<Object> createSendRequest(HttpMethod method, String path, Integer userId,
                                                         @Nullable Map<String, Object> parameters, @Nullable T body) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, getDefaultHeaders(userId));
        ResponseEntity<Object> responseEntity;

        try {
            if (parameters != null) {
                responseEntity = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                responseEntity = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        return buildResponse(responseEntity);
    }

    private HttpHeaders getDefaultHeaders(Integer userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> buildResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        return response.hasBody() ? responseBuilder.body(response.getBody()) : responseBuilder.build();
    }

}
