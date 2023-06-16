package com.example.mailservice.model.modelUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OrderStatus {
    CREATED("Создан"),
    ON_APPROVE("На подтверждении"),
    IN_PROCESS("В процессе"),
    CANCELED("Отменен"),
    DONE("Доставлен");

    final String value;

    @Override
    public String toString() {
        return value;
    }
}
