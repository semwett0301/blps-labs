package com.example.code.model.dto.kafka;

import com.example.code.model.modelUtils.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderDTO {
    private int number;

    private String username;

    private OrderStatus orderStatus;

    private int day;

    private Integer startTime;

    private Integer endTime;

    public OrderDTO(int number, OrderStatus orderStatus, int day, Integer startTime, Integer endTime) {
        this.number = number;
        this.orderStatus = orderStatus;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
