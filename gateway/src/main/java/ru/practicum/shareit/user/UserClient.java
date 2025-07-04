package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;


@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl + API_PREFIX);
    }

    public ResponseEntity<Object> createNewUser(UserCreateDto userCreateDto) {
        return post("", userCreateDto);
    }

    public ResponseEntity<Object> getUser(long userId) {
        return get("/" + userId, userId);
    }

    public void deleteUser(long userId) {
        delete("/" + userId);
    }

    public ResponseEntity<Object> updateUser(UserUpdateDto userUpdateDto, long userId) {
        return patch("/" + userId, userUpdateDto);
    }
}
