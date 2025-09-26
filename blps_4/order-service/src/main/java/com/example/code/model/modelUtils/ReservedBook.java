package com.example.code.model.modelUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ReservedBook {
    private UUID id;
    private int amount;
}