package com.example.code.model.modelUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderStatus {
    CREATED("Создан"),
    ON_APPROVE("На подтверждении"),
    IN_PROCESS("В процессе"),
    CANCELED("Отменен"),
    DONE("Доставлен");

    final String value;

    @JsonValue
    public String getValue() {
        return value;
    }


    @JsonCreator
    public static OrderStatus getOrderStatusFromString(String value) {
        for (OrderStatus dep : OrderStatus.values()) {
            if (dep.getValue().equals(value)) {
                return dep;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
