package user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.dto.UserRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
public class UserRequestJsonTest {
    @Autowired
    private JacksonTester<UserRequest> json;

    @Test
    void testUserRequest() throws Exception {
        UserRequest userRequest = new UserRequest(
                "Пользователь",
                "user@test.com"
        );

        JsonContent<UserRequest> result = json.write(userRequest);

        assertThat(result).extractingJsonPathStringValue("$.name")
                          .isEqualTo("Пользователь");
        assertThat(result).extractingJsonPathStringValue("$.email")
                          .isEqualTo("user@test.com");
    }
}
