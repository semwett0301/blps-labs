package com.example.mailservice.utils.deserializers;

import com.example.mailservice.model.dto.kafka.OrderDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class OrderDTODeserializer implements Deserializer<OrderDTO> {

    @Override
    public OrderDTO deserialize(String topic, byte[] data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(data, OrderDTO.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing JSON message", e);
        }
    }
}
