package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.util.List;
import java.util.Map;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";
    @Autowired
    public UserClient(@Value("${shareit-server.url}") String baseUrl) {
        super(baseUrl + API_PREFIX);
    }

    public ResponseEntity<UserDtoOut> create(UserDtoIn userDtoIn) {
        return post("", userDtoIn, UserDtoOut.class);
    }

    public ResponseEntity<UserDtoOut> update(UserDtoIn userDtoIn, Long userId) {
        return patch("/" + userId.toString(), userDtoIn, userId, UserDtoOut.class);
    }

    public void delete(Long userId) {
        delete("/" + userId.toString(), userId).block();
    }

    public ResponseEntity<UserDtoOut> getById(Long userId) {
        return getAll("/" + userId.toString(), userId, Map.of(), UserDtoOut.class);
    }

    public ResponseEntity<List<UserDtoOut>> getAll() {
        return getAll("",  Map.of(), UserDtoOut[].class);
    }
}
