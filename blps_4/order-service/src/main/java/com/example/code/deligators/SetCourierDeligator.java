package com.example.code.deligators;

import com.example.code.model.exceptions.OrderHasBeenAlreadyAcceptedException;
import com.example.code.model.exceptions.OrderNotFoundException;
import com.example.code.model.modelUtils.Role;
import com.example.code.services.AuthService.AuthService;
import com.example.code.services.DeliveryService.DeliveryService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetCourierDeligator implements JavaDelegate {

    private final DeliveryService deliveryService;
    private final AuthService authService;

    @Autowired
    public SetCourierDeligator(DeliveryService deliveryService, AuthService authService) {
        this.deliveryService = deliveryService;
        this.authService = authService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String username = (String) delegateExecution.getVariable("username");
        Integer orderId = (Integer) delegateExecution.getVariable("order_id");

        try {
            if (authService.findUserRole(username).equals(Role.COURIER)) {
                deliveryService.acceptOrder(orderId);
            } else {
                throw new BpmnError("ACCESS_ERROR");
            }
        } catch (OrderNotFoundException | OrderHasBeenAlreadyAcceptedException e) {
            throw new BpmnError("ACCEPT_ERROR");
        }
    }
}

