package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> saveItem(long ownerId, ItemRequest body) {
        return post("", ownerId, body);
    }

    public ResponseEntity<Object> updateItem(long ownerId, long itemId, ItemRequest body) {
        return patch("/" + itemId, ownerId, body);
    }

    public ResponseEntity<Object> getItemById(long bookingId) {
        return get("/" + bookingId);
    }

    public ResponseEntity<Object> getAllItems(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> getItemByNameOrDescription(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search", parameters);
    }

    public ResponseEntity<Object> saveComment(long userId, long itemId, CommentRequest body) {
        return post("/" + itemId + "/comment", userId, body);
    }
}
