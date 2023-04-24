package com.example.code.api;

import java.util.List;
import java.util.UUID;

import com.example.code.model.dto.RequestCreateOrder;
import com.example.code.model.dto.ResponseAvailableTime;
import com.example.code.model.dto.ResponseCreateOrder;
import com.example.code.model.dto.ResponseOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.exceptions.*;
import com.example.code.model.mappers.OrderMapper;
import com.example.code.model.modelUtils.TimePeriod;
import com.example.code.services.DeliveryService.DeliveryService;
import com.example.code.services.WarehouseService.WarehouseService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Api(value = "Orders", description = "Api related to orders")
public class OrderController {
    private final WarehouseService warehouseService;
    private final DeliveryService deliveryService;

    @Autowired
    public OrderController(WarehouseService warehouseService, DeliveryService deliveryService) {
        this.warehouseService = warehouseService;
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public ResponseEntity<List<ResponseOrder>> getOrders(@CookieValue(value = "user_id") String userId) {
        try {
            return ResponseEntity.ok().body(deliveryService.getOrders(UUID.fromString(userId)));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<ResponseCreateOrder> createOrder(@RequestBody RequestCreateOrder requestCreateOrder, @CookieValue(name = "user_id") UUID userId) {
        try {
            Order order = deliveryService.createOrder(requestCreateOrder.getDay(), userId);
            warehouseService.reserveBooks(requestCreateOrder.getBooks(), order);
            return ResponseEntity.ok().body(OrderMapper.INSTANCE.toResponseCreateOrder(order));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BookIsNotAvailableException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/time/{orderId}")
    public ResponseEntity<ResponseAvailableTime> getAvailableTime(@PathVariable Integer orderId) {
        try {
            return ResponseEntity.ok().body(new ResponseAvailableTime(deliveryService.findAvailableTimePeriods(orderId)));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IncorrectTimePeriodException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/time/{orderId}")
    public ResponseEntity<TimePeriod> setTimeForOrder(@RequestBody TimePeriod timePeriod, @PathVariable int orderId) {
        try {
            deliveryService.setTimeForOrder(orderId, timePeriod);
            deliveryService.choseCourierForOrder(orderId);
            return ResponseEntity.ok().body(timePeriod);
        } catch (TimeIsNotAvailableException e) {
            try {
                deliveryService.unsetTimeForOrder(orderId);
            } catch (OrderNotFoundException ex) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (OrderNotFoundException | OrderHasBeenAlreadyAccepted | IncorrectTimePeriodException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/acceptance/{orderId}")
    public ResponseEntity acceptOrder(@PathVariable int orderId) {
        try {
            deliveryService.acceptOrder(orderId);
            return ResponseEntity.ok().build();
        } catch (OrderNotFoundException | OrderHasBeenAlreadyAccepted e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/acceptance/{orderId}")
    public ResponseEntity declineOrder(@PathVariable int orderId) {
        try {
            deliveryService.choseCourierForOrder(orderId);
            return ResponseEntity.ok().build();
        } catch (OrderNotFoundException | IncorrectTimePeriodException |  OrderHasBeenAlreadyAccepted e) {
            return ResponseEntity.notFound().build();
        } catch (TimeIsNotAvailableException e) {
            try {
                deliveryService.unsetTimeForOrder(orderId);
            } catch (OrderNotFoundException ex) {
                return ResponseEntity.notFound().build();
            }
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseOrder> getOrder(@PathVariable int orderId) {
        try {
            return ResponseEntity.ok().body(deliveryService.getOrder(orderId));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity completeOrder(@PathVariable Integer orderId) {
        try {
            deliveryService.completeOrder(orderId);
            warehouseService.removeReservation(orderId);
            return ResponseEntity.ok().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (OrderHasntBeenAccepted e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity cancelOrder(@PathVariable Integer orderId) {
        try {
            warehouseService.removeReservation(orderId);
            deliveryService.cancelOrder(orderId);
            return ResponseEntity.ok().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
