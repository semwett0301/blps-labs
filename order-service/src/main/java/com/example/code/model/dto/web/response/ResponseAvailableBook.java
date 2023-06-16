package com.example.code.model.dto.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ResponseAvailableBook {
    private UUID id;
    private String name;
    private String description;
    private int minimumAge;
    private int availableAmount;
}
