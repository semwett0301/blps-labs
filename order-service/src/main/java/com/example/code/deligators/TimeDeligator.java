package com.example.code.deligators;

import com.example.code.model.exceptions.*;
import com.example.code.model.modelUtils.TimePeriod;
import com.example.code.services.DeliveryService.DeliveryService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TimeDeligator implements JavaDelegate {

    private final DeliveryService deliveryService;

    @Autowired
    public TimeDeligator(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Integer orderId = (Integer) delegateExecution.getVariable("order_id");
        Integer start = (Integer) delegateExecution.getVariable("start");
        Integer end = (Integer) delegateExecution.getVariable("end");

        try {
            deliveryService.setTimeForOrder(orderId, new TimePeriod(start, end));
        } catch (OrderNotFoundException | TimeIsNotAvailableException | IncorrectTimePeriodException | OrderHasBeenAlreadyAcceptedException | TimeHasBeenAlreadyChosenException e) {
            throw new BpmnError("TIME_SET_ERROR");
        }
    }
}
