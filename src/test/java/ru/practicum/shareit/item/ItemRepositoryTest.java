package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User user;
    Long userId;
    Item item1;
    Item item2;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Vasya");
        user.setEmail("Vasya@mail.ru");
        userId = userRepository.save(user).getId();
        item1 = new Item();
        item1.setName("Вещь");
        item1.setDescription("Очень нужная вещь");
        item1.setOwner(user);
        item1.setAvailable(true);
        ItemRequest ir = itemRequestRepository.save(new ItemRequest(1L, "description", user, LocalDateTime.now()));
        item1.setRequest(ir);
        item1 = itemRepository.save(item1);
        item2 = new Item();
        item2.setName("Штуковина");
        item2.setDescription("Всем нужна такая");
        item2.setOwner(user);
        item2.setAvailable(true);
        ItemRequest ir2 = itemRequestRepository.save(new ItemRequest(2L, "description2", user, LocalDateTime.now()));
        item2.setRequest(ir2);
        itemRepository.save(item2);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void searchItemBySubtring() {
        List<Item> items = itemRepository.searchItemBySubtring("ещь");
        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());

        items = itemRepository.searchItemBySubtring("ужн");
        assertEquals(2, items.size());

        item1.setAvailable(false);
        itemRepository.save(item1);
        items = itemRepository.searchItemBySubtring("ужн");
        assertEquals(1, items.size());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void findAllByRequestIdIn() {
        List<Long> requests = List.of(1L,2L);
        List<Item> items = itemRepository.findAllByRequestIdIn(requests);
        assertEquals(2, items.size());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void findAllByOwnerIdOrderByIdAsc() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        assertEquals(2, items.size());
        assertEquals("Вещь", items.get(0).getName());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void findAllByRequestId() {
        List<Item> items = itemRepository.findAllByRequestId(1L);
        assertEquals(1, items.size());
    }
}