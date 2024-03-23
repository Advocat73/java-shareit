package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void addNewItem() throws Exception {
        ItemDto itemDto = generator.nextObject(ItemDto.class);
        itemDto.setId(0L);
        if (itemDto.getRequestId() < 0) {
            long l = itemDto.getRequestId();
            l *= (-1);
            itemDto.setRequestId(l);
        }

        when(itemService.addNewItem(Mockito.anyLong(), Mockito.any(ItemDto.class))).thenAnswer((Answer<ItemDto>) invocation -> {
            ItemDto itemDtoArg = (ItemDto) invocation.getArguments()[1];
            itemDtoArg.setId(1L);
            return itemDtoArg;
        });

        //создать Post запрос;
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString((itemDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect((jsonPath("$.name", is(itemDto.getName()))))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));


        //отправить запрос;
    }

    @Test
    void updateItem() {
    }

    @Test
    void getUserItems() {
    }

    @Test
    void getItem() {
    }

    @Test
    void searchItemBySubstring() {
    }

    @Test
    void deleteItem() {
    }

    @Test
    void addNewComment() {
    }
}