package com.example.code.api;

import com.example.code.model.dto.request.RequestCreateOrder;
import com.example.code.model.dto.response.ResponseAvailableTime;
import com.example.code.model.dto.response.ResponseCreateOrder;
import com.example.code.model.dto.response.ResponseOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.exceptions.*;
import com.example.code.model.mappers.OrderMapper;
import com.example.code.model.modelUtils.TimePeriod;
import com.example.code.services.DeliveryService.DeliveryService;
import com.example.code.services.WarehouseService.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final WarehouseService warehouseService;
    private final DeliveryService deliveryService;

    @Autowired
    public OrderController(WarehouseService warehouseService, DeliveryService deliveryService) {
        this.warehouseService = warehouseService;
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public List<ResponseOrder> getOrders(@CookieValue(value = "user_id") UUID userId) throws UserNotFoundException {
        return deliveryService.getOrders(userId);
    }

    @PostMapping
    public ResponseCreateOrder createOrder(@RequestBody RequestCreateOrder requestCreateOrder, @CookieValue(name = "user_id") UUID userId) throws UserNotFoundException, BookIsNotAvailableException {
        Order order = deliveryService.createOrder(requestCreateOrder.getDay(), userId);
        warehouseService.reserveBooks(requestCreateOrder.getBooks(), order);
        return OrderMapper.INSTANCE.toResponseCreateOrder(order);
    }

    @GetMapping("/time/{orderId}")
    public ResponseAvailableTime getAvailableTime(@PathVariable Integer orderId) throws OrderNotFoundException, IncorrectTimePeriodException {
        return new ResponseAvailableTime(deliveryService.findAvailableTimePeriods(orderId));
    }

    @PostMapping("/time/{orderId}")
    public TimePeriod setTimeForOrder(@RequestBody TimePeriod timePeriod, @PathVariable int orderId) throws OrderNotFoundException, IncorrectTimePeriodException, OrderHasBeenAlreadyAcceptedException, TimeIsNotAvailableException {
        try {
            deliveryService.setTimeForOrder(orderId, timePeriod);
            deliveryService.choseCourierForOrder(orderId);
            return timePeriod;
        } catch (TimeIsNotAvailableException e) {
            deliveryService.unsetTimeForOrder(orderId);
            throw e;
        }
    }

    @PostMapping("/acceptance/{orderId}")
    public void acceptOrder(@PathVariable int orderId) throws OrderNotFoundException, OrderHasBeenAlreadyAcceptedException {
        deliveryService.acceptOrder(orderId);
    }

    @DeleteMapping("/acceptance/{orderId}")
    public void declineOrder(@PathVariable int orderId) throws OrderNotFoundException, IncorrectTimePeriodException, OrderHasBeenAlreadyAcceptedException, TimeIsNotAvailableException {
        try {
            deliveryService.choseCourierForOrder(orderId);
        } catch (TimeIsNotAvailableException e) {
            deliveryService.unsetTimeForOrder(orderId);
            throw e;
        }
    }

    @GetMapping("/{orderId}")
    public ResponseOrder getOrder(@PathVariable int orderId) throws OrderNotFoundException {
        return deliveryService.getOrder(orderId);
    }

    @PatchMapping("/{orderId}")
    public void completeOrder(@PathVariable Integer orderId) throws OrderNotFoundException {
        warehouseService.removeReservation(orderId);
        deliveryService.completeOrder(orderId);
    }

    @DeleteMapping("/{orderId}")
    public void cancelOrder(@PathVariable Integer orderId) throws OrderNotFoundException {
        warehouseService.removeReservation(orderId);
        deliveryService.cancelOrder(orderId);
    }
}
