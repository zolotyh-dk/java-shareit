package item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.ItemRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
public class ItemRequestJsonTest {

    @Autowired
    private JacksonTester<ItemRequest> json;

    @Test
    void testItemRequest() throws Exception {
        ItemRequest itemRequest = new ItemRequest(
                "Название",
                "Описание",
                true,
                1L
        );

        JsonContent<ItemRequest> result = json.write(itemRequest);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Название");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}
