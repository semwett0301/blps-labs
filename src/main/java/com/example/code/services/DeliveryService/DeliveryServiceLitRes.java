package com.example.code.services.DeliveryService;

import com.example.code.model.entities.Order;
import com.example.code.model.entities.User;
import com.example.code.model.exceptions.IncorrectTimePeriodException;
import com.example.code.model.exceptions.OrderNotFoundException;
import com.example.code.model.exceptions.UserNotFoundException;
import com.example.code.model.modelUtils.OrderStatus;
import com.example.code.model.modelUtils.TimePeriod;
import com.example.code.repositories.OrderRepository;
import com.example.code.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryServiceLitRes implements DeliveryService{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public DeliveryServiceLitRes(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrderResponse(int day) throws UserNotFoundException {
        final UUID userId = UUID.fromString("48f68268-656c-49ff-a220-df39bb9f8241");

        User user = getUserById(userId);
        return createOrder(day, user);
    }

    @Override
    public List<TimePeriod> findAvailableTimePeriods(int orderId) throws OrderNotFoundException, IncorrectTimePeriodException {
        Order order = getOrderById(orderId);
        int day = order.getDay();
        List<User> couriers = getCouriers();

        return findTimePeriodsForCouriers(couriers, day);
    }

    private List<TimePeriod> findTimePeriodsForCouriers(List<User> couriers, int day) throws IncorrectTimePeriodException {
        return TimePeriod.createDefaultListOfTimePeriods().stream()
                .filter(timePeriod -> timePeriod.isAvailableForCouriersInThisDay(couriers, day))
                .collect(Collectors.toList());
    }

    private Order getOrderById(int orderId) throws OrderNotFoundException {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new OrderNotFoundException();
        } else {
            return order.get();
        }
    }

    private Order createOrder(int day, User user) {
        Order newOrder = new Order(day, OrderStatus.CREATED, user);
        orderRepository.save(newOrder);
        return newOrder;
    }

    private User getUserById(UUID userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private List<User> getCouriers() {
        return userRepository.getAllByIsCourier(true);
    }
}
