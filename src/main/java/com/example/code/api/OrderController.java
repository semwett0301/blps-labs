package com.example.code.api;

import com.example.code.model.dto.RequestCreateOrder;
import com.example.code.model.dto.ResponseAvailableTime;
import com.example.code.model.dto.ResponseCreateOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.exceptions.BookIsNotAvailableException;
import com.example.code.model.exceptions.IncorrectTimePeriodException;
import com.example.code.model.exceptions.OrderNotFoundException;
import com.example.code.model.exceptions.UserNotFoundException;
import com.example.code.model.mappers.OrderMapper;
import com.example.code.services.DeliveryService.DeliveryService;
import com.example.code.services.WarehouseService.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<ResponseCreateOrder> createOrder(@RequestBody RequestCreateOrder requestCreateOrder) {
        try {
            Order order = deliveryService.createOrderResponse(requestCreateOrder.getDay());
            warehouseService.reserveBooks(requestCreateOrder.getBooks(), order);
            return ResponseEntity.ok().body(OrderMapper.INSTANCE.toResponseCreateOrderDTO(order));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BookIsNotAvailableException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/available/{orderId}")
    public ResponseEntity<ResponseAvailableTime> getAvailableTime(@PathVariable Integer orderId) {
        try {
            return ResponseEntity.ok().body(new ResponseAvailableTime(deliveryService.findAvailableTimePeriods(orderId)));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IncorrectTimePeriodException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
