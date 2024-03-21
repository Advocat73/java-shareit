package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForGetRequestDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GetItemRequestDto {
    Long id;
    private String description;
    private LocalDateTime created;
    List<ItemForGetRequestDto> items = new ArrayList<>();
}
