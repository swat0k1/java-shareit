package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDto mapToDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static User mapToUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getEmail(), userDto.getName());
    }

}
