package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.UpdateUser;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorageDb userStorageDb;

    public UserService(UserStorageDb userStorageDb) {
        this.userStorageDb = userStorageDb;
    }

    public UserDto getUserById(int id) {
        User user = userStorageDb.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.mapToDto(user);
    }

    public List<UserDto> getUsers() {
        return UserMapper.mapToDtos(userStorageDb.findAll());
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userStorageDb.existsByEmail(userDto.getEmail())) {
            throw new UserDataException("Пользователь с данным email уже существует");
        }
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToDto(userStorageDb.save(user));
    }

    @Transactional
    public UserDto updateUser(int id, UpdateUser updateUser) {
        if (userStorageDb.existsByEmail(updateUser.getEmail())) {
            throw new UserDataException("Пользователь с данным email уже существует");
        }
        User user = userStorageDb.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updateUsersFields(updateUser, user);
        return UserMapper.mapToDto(userStorageDb.save(user));
    }

    @Transactional
    public UserDto deleteUser(int id) {
        User user = userStorageDb.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userStorageDb.deleteById(id);
        return UserMapper.mapToDto(user);
    }

    private void updateUsersFields(UpdateUser updateUser, User user) {

        if (updateUser.getEmail() != null) {
            user.setEmail(updateUser.getEmail());
        }

        if (updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }

    }

}
