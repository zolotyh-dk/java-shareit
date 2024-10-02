package user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
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
    public void testSaveUserWithExistingEmail() {
        UserRequest request = new UserRequest("Тестовый Пользователь", "user@test.com");
        userService.save(request);

        UserRequest duplicateRequest = new UserRequest("Другой Пользователь", "user@test.com");

        Exception exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.save(duplicateRequest));

        assertThat(exception.getMessage(), equalTo("Email: user@test.com уже существует"));
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
    public void testUpdateUserWithExistingEmail() {
        UserRequest request1 = new UserRequest("Пользователь 1", "user1@test.com");
        UserRequest request2 = new UserRequest("Пользователь 2", "user2@test.com");
        userService.save(request1);
        UserResponse userResponse2 = userService.save(request2);

        UserRequest updateRequest = new UserRequest("Обновленный Пользователь", "user1@test.com");

        Exception exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.update(updateRequest, userResponse2.getId()));

        assertThat(exception.getMessage(), equalTo("Email: user1@test.com уже существует"));
    }

    @Test
    public void testUpdateUserWithEmptyName() {
        UserRequest request = new UserRequest("Тестовый Пользователь", "user@test.com");
        UserResponse savedUser = userService.save(request);

        UserRequest updateRequest = new UserRequest("", "newemail@test.com");
        UserResponse updatedUser = userService.update(updateRequest, savedUser.getId());

        assertThat(updatedUser.getName(), equalTo(request.getName())); // Имя должно остаться прежним
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
    public void testGetUserByIdNotFound() {
        long nonExistentId = 999;

        Exception exception = assertThrows(NotFoundException.class, () -> userService.getById(nonExistentId));

        assertThat(exception.getMessage(), equalTo("Пользователь с id: " + nonExistentId + " не найден"));
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
