package com.example.code.model.dto;

import com.example.code.model.modelUtils.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseOrder {
    private int number;

    private OrderStatus orderStatus;

    private int day;

    private Integer startTime;

    private Integer endTime;
}
