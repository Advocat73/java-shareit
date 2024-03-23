package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    User user;
    Long userId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Vasya");
        user.setEmail("Vasya@mail.ru");
        userId = userRepository.save(user).getId();
    }


    @Test
    public void searchItemBySubtring() {
        Item item1 = new Item();
        item1.setName("Вещь");
        item1.setDescription("Очень нужная вещь");
        item1.setOwner(user);
        item1.setAvailable(true);
        item1 = itemRepository.save(item1);
        Item item2 = new Item();
        item2.setName("Штуковина");
        item2.setDescription("Всем нужна такая");
        item2.setOwner(user);
        item2.setAvailable(true);
        itemRepository.save(item2);

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
}