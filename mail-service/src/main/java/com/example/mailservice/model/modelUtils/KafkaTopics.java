package com.example.mailservice.model.modelUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KafkaTopics {
    ORDER_TOPIC("order-topic");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
