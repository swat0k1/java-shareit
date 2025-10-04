package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto mapToDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static List<UserDto> mapToDtos(List<User> users) {
        return users.stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public static User mapToUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getEmail(), userDto.getName());
    }

    public static List<User> mapToUsers(List<UserDto> usersDto) {
        return usersDto.stream()
                .map(UserMapper::mapToUser)
                .collect(Collectors.toList());
    }

}
