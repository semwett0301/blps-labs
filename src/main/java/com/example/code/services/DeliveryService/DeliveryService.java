package com.example.code.services.DeliveryService;

import com.example.code.model.dto.ResponseCreateOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.exceptions.IncorrectTimePeriodException;
import com.example.code.model.exceptions.OrderNotFoundException;
import com.example.code.model.exceptions.UserNotFoundException;
import com.example.code.model.modelUtils.TimePeriod;

import java.util.List;

public interface DeliveryService {
    Order createOrderResponse(int day) throws UserNotFoundException;
    List<TimePeriod> findAvailableTimePeriods(int orderId) throws OrderNotFoundException, IncorrectTimePeriodException;
}
