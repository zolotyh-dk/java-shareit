package user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final UserService userService;
    private final UserRepository userRepository;

    @Test
    public void testSaveUser() {
        UserRequest request = new UserRequest("Тестовый Пользователь", "user@test.com");

        UserResponse savedUser = userService.save(request);
        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), equalTo(request.getName()));
        assertThat(savedUser.getEmail(), equalTo(request.getEmail()));

        User userFromDb = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(userFromDb.getName(), equalTo(request.getName()));
        assertThat(userFromDb.getEmail(), equalTo(request.getEmail()));
    }

    @Test
    public void testUpdateUser() {
        UserRequest saveRequest = new UserRequest("Тестовый Пользователь", "user@test.com");
        UserResponse savedUser = userService.save(saveRequest);

        UserRequest updateRequest = new UserRequest("Обновленный Пользователь", "updated@test.com");
        UserResponse updatedUser = userService.update(updateRequest, savedUser.getId());

        assertThat(updatedUser.getName(), equalTo(updateRequest.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updateRequest.getEmail()));

        User userFromDb = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(userFromDb.getName(), equalTo(updateRequest.getName()));
        assertThat(userFromDb.getEmail(), equalTo(updateRequest.getEmail()));
    }

    @Test
    public void testGetUserById() {
        UserRequest request = new UserRequest("Тестовый Пользователь", "user@test.com");
        UserResponse savedUser = userService.save(request);

        UserResponse userFromService = userService.getById(savedUser.getId());

        assertThat(userFromService.getId(), equalTo(savedUser.getId()));
        assertThat(userFromService.getName(), equalTo(savedUser.getName()));
        assertThat(userFromService.getEmail(), equalTo(savedUser.getEmail()));
    }

    @Test
    public void testDeleteUser() {
        UserRequest request = new UserRequest("Тестовый Пользователь", "user@test.com");
        UserResponse savedUser = userService.save(request);

        userService.delete(savedUser.getId());

        User deletedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(deletedUser, nullValue());
    }
}
