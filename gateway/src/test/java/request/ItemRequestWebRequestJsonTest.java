package request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.dto.ItemRequestWebRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
public class ItemRequestWebRequestJsonTest {

    @Autowired
    private JacksonTester<ItemRequestWebRequest> json;

    @Test
    void testItemRequestWebRequest() throws Exception {
        ItemRequestWebRequest itemRequestWebRequest = new ItemRequestWebRequest(
                "Описание нужной вещи"
        );

        JsonContent<ItemRequestWebRequest> result = json.write(itemRequestWebRequest);

        assertThat(result).extractingJsonPathStringValue("$.description")
                          .isEqualTo("Описание нужной вещи");
    }
}
