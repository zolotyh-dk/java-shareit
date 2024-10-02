package item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidBookingDateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Test
    public void testSaveItem() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        ItemWebRequest request = new ItemWebRequest("Название вещи", "Описание вещи", true, null);
        ItemWebResponse response = itemService.save(request, savedOwner.getId());

        assertThat(response.getId(), notNullValue());
        assertThat(response.getName(), equalTo(request.getName()));
        assertThat(response.getDescription(), equalTo(request.getDescription()));

        Item savedItem = itemRepository.findById(response.getId()).orElseThrow();
        assertThat(savedItem.getName(), equalTo(request.getName()));
        assertThat(savedItem.getDescription(), equalTo(request.getDescription()));
    }

    @Test
    public void testSaveItemWithRequest() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User requestor = new User();
        requestor.setName("Запрашивающий");
        requestor.setEmail("requestor@test.com");
        User savedRequestor = userRepository.save(requestor);

        ItemRequest request = new ItemRequest();
        request.setDescription("Нужна дрель");
        request.setRequestor(savedRequestor);
        ItemRequest savedRequest = requestRepository.save(request);

        ItemWebRequest itemRequest = new ItemWebRequest("Дрель", "Мощная дрель", true, savedRequest.getId());

        ItemWebResponse itemResponse = itemService.save(itemRequest, savedOwner.getId());

        assertThat(itemResponse.getId(), notNullValue());
        assertThat(itemResponse.getName(), equalTo(itemRequest.getName()));
        assertThat(itemResponse.getDescription(), equalTo(itemRequest.getDescription()));

        Item savedItem = itemRepository.findById(itemResponse.getId()).orElseThrow();
        assertThat(savedItem.getName(), equalTo(itemRequest.getName()));
        assertThat(savedItem.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(savedItem.getRequest().getId(), equalTo(savedRequest.getId()));
    }

    @Test
    public void testUpdateItem() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        ItemWebRequest initialRequest = new ItemWebRequest("Старая вещь", "Старое описание", true, null);
        ItemWebResponse savedItem = itemService.save(initialRequest, savedOwner.getId());

        ItemWebRequest updateRequest = new ItemWebRequest("Новая вещь", "Новое описание", false, null);
        ItemWebResponse updatedResponse = itemService.update(updateRequest, savedItem.getId(), savedOwner.getId());

        assertThat(updatedResponse.getName(), equalTo(updateRequest.getName()));
        assertThat(updatedResponse.getDescription(), equalTo(updateRequest.getDescription()));
        assertThat(updatedResponse.getAvailable(), equalTo(updateRequest.getAvailable()));

        Item updatedItemInDb = itemRepository.findById(savedItem.getId()).orElseThrow();
        assertThat(updatedItemInDb.getName(), equalTo(updateRequest.getName()));
        assertThat(updatedItemInDb.getDescription(), equalTo(updateRequest.getDescription()));
        assertThat(updatedItemInDb.getAvailable(), equalTo(updateRequest.getAvailable()));
    }

    @Test
    public void testUnauthorizedUpdateItem() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User notOwner = new User();
        notOwner.setName("Не владелец");
        notOwner.setEmail("notowner@test.com");
        User savedNotOwner = userRepository.save(notOwner);

        ItemWebRequest initialRequest = new ItemWebRequest("Вещь", "Описание вещи", true, null);
        ItemWebResponse savedItem = itemService.save(initialRequest, savedOwner.getId());

        // Попытка пользователя (не владельца) обновить вещь
        ItemWebRequest updateRequest = new ItemWebRequest("Новая вещь", "Новое описание", false, null);

        assertThrows(UnauthorizedAccessException.class, () ->
                itemService.update(updateRequest, savedItem.getId(), savedNotOwner.getId()));
    }

    @Test
    public void testNotFoundUpdateItem() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        ItemWebRequest updateRequest = new ItemWebRequest("Новая вещь", "Новое описание", false, null);

        assertThrows(NotFoundException.class, () -> {
            itemService.update(updateRequest, 999L, savedOwner.getId());  // 999L — несуществующий ID вещи
        });
    }

    @Test
    public void testGetById() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        ItemWebRequest request = new ItemWebRequest("Название вещи", "Описание вещи", true, null);
        ItemWebResponse savedItem = itemService.save(request, savedOwner.getId());

        ItemDetailResponse response = itemService.getById(savedItem.getId());

        assertThat(response.getName(), equalTo(request.getName()));
        assertThat(response.getDescription(), equalTo(request.getDescription()));
        assertThat(response.getAvailable(), equalTo(request.getAvailable()));
    }

    @Test
    public void testAddComment() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        ItemWebRequest itemRequest = new ItemWebRequest("Название вещи", "Описание вещи", true, null);
        ItemWebResponse itemWebResponse = itemService.save(itemRequest, savedOwner.getId());

        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(itemWebResponse.getId()).orElseThrow());
        booking.setBooker(booker);
        booking.setStart(Instant.now().minusSeconds(3600 * 24)); // Начало аренды было вчера
        booking.setEnd(Instant.now().minusSeconds(3600)); // Завершено час назад
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentRequest commentRequest = new CommentRequest("Отличная вещь!");
        CommentResponse commentResponse = itemService.addComment(itemWebResponse.getId(), savedBooker.getId(), commentRequest);
        assertThat(commentResponse.getText(), equalTo(commentRequest.getText()));
        assertThat(commentResponse.getAuthorName(), equalTo(savedBooker.getName()));

        Comment savedComment = commentRepository.findById(commentResponse.getId()).orElseThrow();
        assertThat(savedComment.getText(), equalTo(commentRequest.getText()));
        assertThat(savedComment.getAuthor().getId(), equalTo(booker.getId()));
        assertThat(savedComment.getItem().getId(), equalTo(itemWebResponse.getId()));
    }

    @Test
    public void testAddCommentWithoutCompletedBooking() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        // Создание активного бронирования (аренда еще не завершена)
        Booking booking = new Booking();
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        booking.setStart(Instant.now().plusSeconds(3600)); // Аренда начнется через час
        booking.setEnd(Instant.now().plusSeconds(7200));   // Аренда завершится через два часа
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentRequest commentRequest = new CommentRequest("Отличная вещь!");

        assertThrows(InvalidBookingDateException.class, () ->
                itemService.addComment(savedItem.getId(), savedBooker.getId(), commentRequest));
    }

    @Test
    public void testGetAllItemsWithBookingsAndComments() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@test.com");
        User savedBooker = userRepository.save(booker);

        Item item1 = new Item();
        item1.setName("Вещь 1");
        item1.setDescription("Описание 1");
        item1.setAvailable(true);
        item1.setOwner(savedOwner);
        Item savedItem1 = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Вещь 1");
        item2.setDescription("Описание 1");
        item2.setAvailable(true);
        item2.setOwner(savedOwner);
        Item savedItem2 = itemRepository.save(item2);

        Booking booking1 = new Booking();
        booking1.setStart(Instant.now().minusSeconds(3600));
        booking1.setEnd(Instant.now().minusSeconds(1800));
        booking1.setItem(savedItem1);
        booking1.setBooker(savedBooker);
        booking1.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(Instant.now().minusSeconds(7200));
        booking2.setEnd(Instant.now().minusSeconds(3600));
        booking2.setItem(savedItem2);
        booking2.setBooker(savedBooker);
        booking2.setStatus(BookingStatus.APPROVED);

        //Сохраняем комментарий к первой вещи после бронирования
        CommentRequest commentRequest = new CommentRequest("Отличная вещь!");
        itemService.addComment(savedItem1.getId(), savedBooker.getId(), commentRequest);

        Collection<ItemDetailResponse> items = itemService.getAll(savedOwner.getId());

        assertThat(items.size(), equalTo(2));

        ItemDetailResponse itemResponse1 = items.stream()
                .filter(item -> item.getId() == savedItem1.getId())
                .findFirst()
                .orElseThrow();
        ItemDetailResponse itemResponse2 = items.stream()
                .filter(item -> item.getId() == savedItem2.getId())
                .findFirst()
                .orElseThrow();

        // Проверяем, что для первой вещи есть комментарий
        assertThat(itemResponse1.getComments().size(), equalTo(1));
        assertThat(itemResponse1.getComments().get(0).getText(), equalTo("Отличная вещь!"));

        // Проверяем, что для второй вещи комментариев нет
        assertThat(itemResponse2.getComments().isEmpty(), equalTo(true));
    }

    @Test
    public void testGetByNameOrDescriptionWithPartialMatch() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        userRepository.save(owner);

        Item item1 = new Item();
        item1.setName("Шуруповерт");
        item1.setDescription("Инструмент для ремонта");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Полезный инструмент для работы");
        item2.setAvailable(true);
        item2.setOwner(owner);
        itemRepository.save(item2);

        // Выполняем поиск по подстроке "инструмент"
        Collection<ItemWebResponse> items = itemService.getByNameOrDescription("инструмент");

        // Проверяем, что результат содержит обе вещи с упоминанием "инструмент" в описании
        assertThat(items, hasSize(2));
    }

    @Test
    public void testGetByNameOrDescriptionWithEmptyQuery() {
        Collection<ItemWebResponse> items = itemService.getByNameOrDescription("");
        assertThat(items, empty());
    }

}
