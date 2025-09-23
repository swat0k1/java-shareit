package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.UpdateUser;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto getUserById(int id) {
        User user = userStorage.getUserById(id);
        return UserMapper.mapToDto(user);
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers()
                .stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        userStorage.createUser(user);
        return UserMapper.mapToDto(user);
    }

    public UserDto updateUser(int id, UpdateUser updateUser) {
        User user = userStorage.getUserById(id);
        User updatedUser = new User(user.getId(), user.getEmail(), user.getName());
        updateUsersFields(updateUser, updatedUser);
        userStorage.updateUser(updatedUser);
        return UserMapper.mapToDto(updatedUser);
    }

    private void updateUsersFields(UpdateUser updateUser, User user) {

        if (updateUser.getEmail() != null) {
            user.setEmail(updateUser.getEmail());
        }

        if (updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }

    }

    public UserDto deleteUser(int id) {
        User user = userStorage.deleteUser(id);
        return UserMapper.mapToDto(user);
    }

}
