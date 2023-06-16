package com.example.code.api;

import com.example.code.model.dto.response.ResponseAvailableTime;
import com.example.code.model.exceptions.*;
import com.example.code.model.modelUtils.TimePeriod;
import com.example.code.services.DeliveryService.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/time")
public class TimeController {

    private final DeliveryService deliveryService;

    @Autowired
    public TimeController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/{orderId}")
    public ResponseAvailableTime getAvailableTime(@PathVariable Integer orderId) throws OrderNotFoundException, IncorrectTimePeriodException {
        return new ResponseAvailableTime(deliveryService.findAvailableTimePeriods(orderId));
    }

    @PostMapping("/{orderId}")
    public TimePeriod setTimeForOrder(@RequestBody TimePeriod timePeriod, @PathVariable int orderId) throws OrderNotFoundException, IncorrectTimePeriodException, OrderHasBeenAlreadyAcceptedException, TimeIsNotAvailableException, TimeHasBeenAlreadyChosenException {
        deliveryService.setTimeForOrder(orderId, timePeriod);
        return timePeriod;
    }
}
