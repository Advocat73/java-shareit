package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesBookingDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        when(itemService.addNewItem(anyLong(), any(ItemDto.class))).thenAnswer((Answer<ItemDto>) invocation -> {
            ItemDto itemDtoArg = (ItemDto) invocation.getArguments()[1];
            itemDtoArg.setId(1L);
            return itemDtoArg;
        });

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
    }

    @Test
    void updateItem() throws Exception {
        ItemDto itemDto = generator.nextObject(ItemDto.class);
        itemDto.setId(1L);
        if (itemDto.getRequestId() < 0) {
            long l = itemDto.getRequestId();
            l *= (-1);
            itemDto.setRequestId(l);
        }

        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong())).thenAnswer((Answer<ItemDto>) invocation -> {
            ItemDto itemDtoArg = (ItemDto) invocation.getArguments()[1];
            itemDtoArg.setId((Long) invocation.getArguments()[2]);
            return itemDtoArg;
        });

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString((itemDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect((jsonPath("$.name", is(itemDto.getName()))))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getItem() throws Exception {
        ItemWithDatesBookingDto itemDto = generator.nextObject(ItemWithDatesBookingDto.class);
        itemDto.setId(1L);
        if (itemDto.getRequestId() < 0) {
            long l = itemDto.getRequestId();
            l *= (-1);
            itemDto.setRequestId(l);
        }
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDto);
        mvc.perform(get("/items/{itemId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect((jsonPath("$.name", is(itemDto.getName()))))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getUserItems() throws Exception {
        ItemWithDatesBookingDto itemDto = generator.nextObject(ItemWithDatesBookingDto.class);
        itemDto.setId(1L);
        if (itemDto.getRequestId() < 0) {
            long l = itemDto.getRequestId();
            l *= (-1);
            itemDto.setRequestId(l);
        }
        ItemWithDatesBookingDto itemDto2 = generator.nextObject(ItemWithDatesBookingDto.class);
        itemDto2.setId(2L);
        if (itemDto2.getRequestId() < 0) {
            long l = itemDto2.getRequestId();
            l *= (-1);
            itemDto2.setRequestId(l);
        }
        List<ItemWithDatesBookingDto> itemDtos = List.of(itemDto, itemDto2);
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt())).thenReturn(itemDtos);
        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect((jsonPath("$[0].name", is(itemDtos.get(0).getName()))))
                .andExpect(jsonPath("$[0].description", is(itemDtos.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtos.get(0).getAvailable())));
    }

    @Test
    void searchItemBySubstring() throws Exception {
        ItemDto itemDto = generator.nextObject(ItemDto.class);
        itemDto.setId(1L);
        if (itemDto.getRequestId() < 0) {
            long l = itemDto.getRequestId();
            l *= (-1);
            itemDto.setRequestId(l);
        }
        ItemDto itemDto2 = generator.nextObject(ItemDto.class);
        itemDto2.setId(2L);
        if (itemDto2.getRequestId() < 0) {
            long l = itemDto2.getRequestId();
            l *= (-1);
            itemDto2.setRequestId(l);
        }
        List<ItemDto> itemDtos = List.of(itemDto, itemDto2);
        when(itemService.searchItemBySubstring(anyString())).thenReturn(itemDtos);
        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "text")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect((jsonPath("$[0].name", is(itemDtos.get(0).getName()))))
                .andExpect(jsonPath("$[0].description", is(itemDtos.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtos.get(0).getAvailable())));
    }

    @Test
    void deleteItem() throws Exception {
        mvc.perform(delete("/items/{itemId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void addNewComment() throws Exception {
        CommentDto commentDto = generator.nextObject(CommentDto.class);
        when(itemService.addNewComment(anyLong(), anyLong(), any(CommentDto.class))).thenAnswer((Answer<CommentDto>) invocation -> {
            CommentDto commentDtoArg = (CommentDto) invocation.getArguments()[2];
            commentDtoArg.setId(1L);
            return commentDtoArg;
        });
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString((commentDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect((jsonPath("$.text", is(commentDto.getText()))))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}