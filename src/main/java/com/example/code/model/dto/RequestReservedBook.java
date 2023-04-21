package com.example.code.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class RequestReservedBook {
    private UUID id;
    private int amount;
}
