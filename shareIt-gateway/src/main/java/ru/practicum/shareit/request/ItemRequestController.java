package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addNewRequestItem(@NotNull(message = "Не указан Id пользователя при запросе на поиск вещи")
                                                    @RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("REQUESTS_КОНТРОЛЛЕР: POST-запрос по эндпоинту /requests");
        return itemRequestClient.addNewRequestItem(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestItems(@NotNull(message = "Не указан Id пользователя при запросе на поиск ответов на запрос")
                                                  @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("REQUESTS_КОНТРОЛЛЕР: GET-запрос по эндпоинту /requests");
        return itemRequestClient.getRequesterItems(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsFromIndex(@NotNull(message = "Не указан Id пользователя при запросе на поиск всех запросов")
                                                              @RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                              @PositiveOrZero(message = "Индекс первой страницы не может быть отрицательным")
                                                              @RequestParam(defaultValue = "0", required = false) Integer from,
                                                              @Positive(message = "Количество ответов на запрос на странице должно быть положительным")
                                                              @RequestParam(defaultValue = "1", required = false) Integer size) {
        log.info("REQUESTS_КОНТРОЛЛЕР: GET-запрос по эндпоинту /requests/all?from={}&size={}", from, size);
        return itemRequestClient.getAllItemRequestsFromIndexPageable(requesterId, from, size);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestByRequestId(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                            @PathVariable Long requestId) {
        log.info("REQUESTS_КОНТРОЛЛЕР: GET-запрос по эндпоинту /requests/{}", requestId);
        return itemRequestClient.getItemRequest(requesterId, requestId);
    }
}
