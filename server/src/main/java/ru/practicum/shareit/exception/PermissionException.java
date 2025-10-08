package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PermissionException extends RuntimeException {
    public PermissionException(String message) {
        super("Permission exception: " + message);
    }
}
