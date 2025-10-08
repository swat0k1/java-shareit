package ru.practicum.shareit.exception;

public class UserDataException extends RuntimeException {
    public UserDataException(String message) {
        super("User data exception: " + message);
    }
}
