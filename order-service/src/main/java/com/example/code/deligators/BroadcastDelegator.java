package com.example.code.deligators;

import com.example.code.model.entities.Order;
import com.example.code.model.entities.UserInfo;
import com.example.code.repositories.UserRepository;
import com.example.code.services.DeliveryService.DeliveryServiceLitRes;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BroadcastDelegator implements JavaDelegate {

    private final DeliveryServiceLitRes deliveryServiceLitRes;
    private final UserRepository userRepository;

    public BroadcastDelegator(DeliveryServiceLitRes deliveryServiceLitRes, UserRepository userRepository) {
        this.deliveryServiceLitRes = deliveryServiceLitRes;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Map<String, String> usersToMessages = new HashMap<>();
        Map<Integer, Order> ordersMap = deliveryServiceLitRes.ordersMap;
        ordersMap.keySet().forEach(a -> {
            Order orderDTO = ordersMap.get(a);
            UserInfo userInfo = userRepository.findByUsername(orderDTO.getUser().getUsername()).orElseThrow();
            if (userInfo.isNotificated()) {
                String email = userInfo.getEmail();
                usersToMessages.put(email, orderDTO.getUser().getEmail());

            }
        });

        usersToMessages.keySet().forEach(email -> System.out.println(usersToMessages.get(email)));
    }
}
