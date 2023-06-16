package com.example.code.utils.serializers;

import com.example.code.model.dto.kafka.OrderDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class OrderDTOSerializers implements Serializer<OrderDTO> {
    @Override
    public byte[] serialize(String s, OrderDTO orderDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsBytes(orderDTO);
        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }
}
