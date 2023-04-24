package com.example.code.services.DeliveryService;

import com.example.code.model.dto.ResponseOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.exceptions.*;
import com.example.code.model.modelUtils.TimePeriod;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {

    List<ResponseOrder> getOrders(UUID userId) throws UserNotFoundException;
    Order createOrder(int day, UUID userId) throws UserNotFoundException;
    List<TimePeriod> findAvailableTimePeriods(int orderId) throws OrderNotFoundException, IncorrectTimePeriodException;
    void cancelOrder(int orderId) throws OrderNotFoundException;

    void setTimeForOrder(int orderId, TimePeriod timePeriod) throws OrderNotFoundException;

    void unsetTimeForOrder(int orderIs) throws OrderNotFoundException;

    void choseCourierForOrder(int orderId) throws OrderNotFoundException, TimeIsNotAvailableException, IncorrectTimePeriodException, OrderHasBeenAlreadyAccepted;

    ResponseOrder getOrder(int orderId) throws OrderNotFoundException;

    void acceptOrder(int orderId) throws OrderNotFoundException, OrderHasBeenAlreadyAccepted;

    void completeOrder(int orderId) throws OrderNotFoundException;
}
