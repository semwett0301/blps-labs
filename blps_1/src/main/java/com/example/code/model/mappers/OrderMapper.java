package com.example.code.model.mappers;

import com.example.code.model.dto.ResponseCreateOrder;
import com.example.code.model.dto.ResponseOrder;
import com.example.code.model.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);


    ResponseCreateOrder toResponseCreateOrder(Order order);

    @Mappings({
            @Mapping(source = "order.number", target = "number"),
            @Mapping(source = "order.orderStatus", target = "orderStatus"),
            @Mapping(source = "order.day", target = "day"),
            @Mapping(source = "order.startTime", target = "startTime"),
            @Mapping(source = "order.endTime", target = "endTime"),
    })
    ResponseOrder toResponseOrders(Order order);
}
