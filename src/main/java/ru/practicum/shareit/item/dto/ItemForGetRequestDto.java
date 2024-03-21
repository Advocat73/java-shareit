package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemForGetRequestDto {
    private Long id;
    private String name;
    String description;
    private Long requestId;
    Boolean available;
}
