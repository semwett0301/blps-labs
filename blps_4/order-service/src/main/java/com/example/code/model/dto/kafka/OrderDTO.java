package com.example.code.model.dto.kafka;

import com.example.code.model.modelUtils.OrderStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private int number;

    private String username;

    private String orderStatus;

    private int day;

    private Integer startTime;

    private Integer endTime;

}
