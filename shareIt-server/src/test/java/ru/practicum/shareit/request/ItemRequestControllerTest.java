package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void addNewUser() throws Exception {
        ItemRequestDto itemRequestDto = createItemRequestDto();
        when(requestService.addNewRequestItem(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString((itemRequestDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect((jsonPath("$.description", is(itemRequestDto.getDescription()))));
    }

    @Test
    void getRequestItems() throws Exception {
        GetItemRequestDto getItemRequestDto = createGetItemRequestDto();
        GetItemRequestDto getItemRequestDto1 = createGetItemRequestDto();
        getItemRequestDto1.setId(2L);
        List<GetItemRequestDto> items = List.of(getItemRequestDto, getItemRequestDto1);
        when(requestService.getRequesterItems(anyLong())).thenReturn(items);
        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect((jsonPath("$[0].description", is(getItemRequestDto.getDescription()))));
    }

    @Test
    void getAllItemRequestsFromIndex() throws Exception {
        GetItemRequestDto getItemRequestDto = createGetItemRequestDto();
        GetItemRequestDto getItemRequestDto1 = createGetItemRequestDto();
        getItemRequestDto1.setId(2L);
        List<GetItemRequestDto> items = List.of(getItemRequestDto, getItemRequestDto1);
        when(requestService.getAllItemRequestsFromIndexPageable(anyLong(), anyInt(), anyInt())).thenReturn(items);
        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect((jsonPath("$[0].description", is(getItemRequestDto.getDescription()))));
    }

    @Test
    void getItemRequestByRequestId() throws Exception {
        GetItemRequestDto getItemRequestDto = createGetItemRequestDto();
        when(requestService.getItemRequest(anyLong(), anyLong())).thenReturn(getItemRequestDto);
        mvc.perform(get("/requests/{requestId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect((jsonPath("$.description", is(getItemRequestDto.getDescription()))));
    }

    private ItemRequestDto createItemRequestDto() {
        ItemRequestDto itemRequestDto = generator.nextObject(ItemRequestDto.class);
        itemRequestDto.setId(1L);
        return itemRequestDto;
    }

    private GetItemRequestDto createGetItemRequestDto() {
        GetItemRequestDto getItemRequestDto = generator.nextObject(GetItemRequestDto.class);
        getItemRequestDto.setId(1L);
        return getItemRequestDto;
    }
}