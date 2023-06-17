package com.example.code.model.mappers;

import com.example.code.model.dto.kafka.OrderDTO;
import com.example.code.model.dto.web.response.ResponseCreateOrder;
import com.example.code.model.dto.web.response.ResponseOrder;
import com.example.code.model.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    ResponseCreateOrder toResponseCreateOrder(Order order);

    ResponseOrder toResponseOrders(Order order);
    @Mappings({
            @Mapping(source = "order.number", target = "number"),
            @Mapping(source = "order.orderStatus.value", target = "orderStatus"),
            @Mapping(source = "order.day", target = "day"),
            @Mapping(source = "order.startTime", target = "startTime"),
            @Mapping(source = "order.endTime", target = "endTime"),
    })
    OrderDTO toOrderDTO(Order order);
}
