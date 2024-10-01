package request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemForItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestWebRequest;
import ru.practicum.shareit.request.dto.ItemRequestWebResponse;
import ru.practicum.shareit.request.dto.ItemRequestWebResponseWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final ItemRequestService requestService;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    public void testSaveItemRequest() {
        User requestor = new User();
        requestor.setName("Запросчик");
        requestor.setEmail("requestor@test.com");
        User savedRequestor = userRepository.save(requestor);

        ItemRequestWebRequest request = new ItemRequestWebRequest("Нужен ноутбук");
        ItemRequestWebResponse response = requestService.save(request, savedRequestor.getId());

        assertThat(response.getId(), notNullValue());
        assertThat(response.getDescription(), equalTo(request.getDescription()));

        ItemRequest savedRequest = requestRepository.findById(response.getId()).orElseThrow();
        assertThat(savedRequest.getDescription(), equalTo(request.getDescription()));
    }

    @Test
    public void testGetAllRequestsByRequestor() {
        User requestor = new User();
        requestor.setName("Запросчик");
        requestor.setEmail("requestor@test.com");
        User savedRequestor = userRepository.save(requestor);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Нужен телефон");
        request1.setRequestor(savedRequestor);
        requestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Нужен велосипед");
        request2.setRequestor(savedRequestor);
        requestRepository.save(request2);

        Collection<ItemRequestWebResponseWithItems> requests = requestService.getAllByRequestor(savedRequestor.getId());
        assertThat(requests.size(), equalTo(2));
    }

    @Test
    public void testGetAllRequestsExcludingRequestor() {
        User requestor = new User();
        requestor.setName("Запросчик");
        requestor.setEmail("requestor@test.com");
        User savedRequestor = userRepository.save(requestor);

        User otherUser = new User();
        otherUser.setName("Другой пользователь");
        otherUser.setEmail("otheruser@test.com");
        User savedOtherUser = userRepository.save(otherUser);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Нужен ноутбук");
        request1.setRequestor(savedOtherUser);
        requestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Нужна гитара");
        request2.setRequestor(savedOtherUser);
        requestRepository.save(request2);

        Collection<ItemRequestWebResponse> requests = requestService.getAll(savedRequestor.getId());
        assertThat(requests.size(), equalTo(2));
    }

    @Test
    public void testGetRequestById() {
        User requestor = new User();
        requestor.setName("Запросчик");
        requestor.setEmail("requestor@test.com");
        User savedRequestor = userRepository.save(requestor);

        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@test.com");
        User savedOwner = userRepository.save(requestor);

        ItemRequest request = new ItemRequest();
        request.setDescription("Нужен ноутбук");
        request.setRequestor(savedRequestor);
        ItemRequest savedRequest = requestRepository.save(request);

        Item item = new Item();
        item.setName("Ноутбук");
        item.setDescription("Хороший ноутбук");
        item.setOwner(savedOwner);
        item.setAvailable(true);
        item.setRequest(savedRequest);
        itemRepository.save(item);

        ItemRequestWebResponseWithItems response = requestService.getRequestById(savedRequestor.getId(), savedRequest.getId());

        assertThat(response.getDescription(), equalTo(savedRequest.getDescription()));
        List<ItemForItemRequest> items = response.getItems();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getName(), equalTo("Ноутбук"));
    }
}
