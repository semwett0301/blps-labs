package com.example.code.deligators;

import com.example.code.model.exceptions.*;
import com.example.code.model.modelUtils.Role;
import com.example.code.model.modelUtils.TimePeriod;
import com.example.code.services.AuthService.AuthService;
import com.example.code.services.DeliveryService.DeliveryService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
public class TimeDeligator implements JavaDelegate {

    private final DeliveryService deliveryService;

    private final AuthService authService;


    @Autowired
    public TimeDeligator(DeliveryService deliveryService, AuthService authService) {
        this.deliveryService = deliveryService;
        this.authService = authService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String username = (String) delegateExecution.getVariable("username");

        Integer orderId = (Integer) delegateExecution.getVariable("order_id");
        Integer start = (Integer) delegateExecution.getVariable("start");
        Integer end = (Integer) delegateExecution.getVariable("end");

        try {
            if (Objects.equals(authService.findUserRole(username), Role.USER)) {
                deliveryService.setTimeForOrder(orderId, new TimePeriod(start, end));
            } else {
                throw new BpmnError("ACCESS_ERROR");
            }
        } catch (OrderNotFoundException | TimeIsNotAvailableException | IncorrectTimePeriodException | OrderHasBeenAlreadyAcceptedException | TimeHasBeenAlreadyChosenException e) {
            throw new BpmnError("TIME_SET_ERROR");
        }
    }
}
