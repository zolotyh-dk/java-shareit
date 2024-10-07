package item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.CommentRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
public class CommentRequestJsonTest {

    @Autowired
    private JacksonTester<CommentRequest> json;

    @Test
    void testCommentRequest() throws Exception {
        CommentRequest commentRequest = new CommentRequest("Это тестовый комментарий");
        JsonContent<CommentRequest> result = json.write(commentRequest);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Это тестовый комментарий");
    }
}
