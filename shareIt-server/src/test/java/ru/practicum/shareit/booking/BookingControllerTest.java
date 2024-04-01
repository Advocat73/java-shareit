package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void addNewBooking() throws Exception {
        BookingDto bookingDto = createBookingDto();
        when(bookService.addNewBooking(anyLong(), any(BookingDto.class))).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString((bookingDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect((jsonPath("$.bookerId", is(bookingDto.getBookerId()))))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));
    }

    @Test
    void updateBooking() throws Exception {
        BookingDto bookingDto = createBookingDto();
        when(bookService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .content(mapper.writeValueAsString((bookingDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect((jsonPath("$.bookerId", is(bookingDto.getBookerId()))))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));
    }

    @Test
    void getBooking() throws Exception {
        BookingDto bookingDto = createBookingDto();
        when(bookService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect((jsonPath("$.bookerId", is(bookingDto.getBookerId()))))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));
    }

    @Test
    void getBookerBookings() throws Exception {
        BookingDto bookingDto1 = createBookingDto();
        BookingDto bookingDto2 = createBookingDto();
        bookingDto2.setId(2L);
        List<BookingDto> bookings = List.of(bookingDto1, bookingDto2);
        when(bookService.getBookerBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookings);
        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "state")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect((jsonPath("$[0].bookerId", is(bookingDto1.getBookerId()))))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto1.getItemId())))
                .andExpect(jsonPath("$[0].status", is(bookingDto1.getStatus())));
    }

    @Test
    void getOwnerItemBookings() throws Exception {
        BookingDto bookingDto1 = createBookingDto();
        BookingDto bookingDto2 = createBookingDto();
        bookingDto2.setId(2L);
        List<BookingDto> bookings = List.of(bookingDto1, bookingDto2);
        when(bookService.getOwnerItemBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookings);
        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "state")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect((jsonPath("$[0].bookerId", is(bookingDto1.getBookerId()))))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto1.getItemId())))
                .andExpect(jsonPath("$[0].status", is(bookingDto1.getStatus())));
    }

    private BookingDto createBookingDto() {
        BookingDto bookingDto = generator.nextObject(BookingDto.class);
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            LocalDateTime tmp = bookingDto.getStart();
            bookingDto.setStart(bookingDto.getEnd());
            bookingDto.setEnd(tmp);
        }
        bookingDto.setId(1L);
        if (bookingDto.getBookerId() < 0) {
            long l = bookingDto.getBookerId();
            l *= (-1);
            bookingDto.setBookerId(l);
        }
        if (bookingDto.getItemId() < 0) {
            long l = bookingDto.getItemId();
            l *= (-1);
            bookingDto.setItemId(l);
        }
        bookingDto.setStatus("ALL");
        return bookingDto;
    }
}