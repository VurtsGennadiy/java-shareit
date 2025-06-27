package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl + API_PREFIX);
    }

    public ResponseEntity<Object> createNewItemRequest(long userId, ItemRequestCreateDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> getUserItemRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllItemRequests(long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getItemRequest(long requestId) {
        return get("/" + requestId);
    }
}
