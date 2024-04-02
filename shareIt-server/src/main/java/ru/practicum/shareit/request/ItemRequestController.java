package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

@Validated
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto addNewRequestItem(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("REQUESTS_КОНТРОЛЛЕР: POST-запрос по эндпоинту /requests");
        return requestService.addNewRequestItem(requesterId, itemRequestDto);
    }

    @GetMapping
    public List<GetItemRequestDto> getRequestItems(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("REQUESTS_КОНТРОЛЛЕР: GET-запрос по эндпоинту /requests");
        return requestService.getRequesterItems(requesterId);
    }

    @GetMapping("/all")
    public List<GetItemRequestDto> getAllItemRequestsFromIndex(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                                               @RequestParam(defaultValue = "1", required = false) Integer size) {
        log.info("REQUESTS_КОНТРОЛЛЕР: GET-запрос по эндпоинту /requests/all?from={}&size={}", from, size);
        return requestService.getAllItemRequestsFromIndexPageable(requesterId, from, size);
    }


    @GetMapping("/{requestId}")
    public GetItemRequestDto getItemRequestByRequestId(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                       @PathVariable Long requestId) {
        log.info("REQUESTS_КОНТРОЛЛЕР: GET-запрос по эндпоинту /requests/{}", requestId);
        return requestService.getItemRequest(requesterId, requestId);
    }
}
