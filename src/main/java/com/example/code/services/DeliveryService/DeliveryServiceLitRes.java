package com.example.code.services.DeliveryService;

import com.example.code.model.dto.ResponseOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.entities.User;
import com.example.code.model.exceptions.*;
import com.example.code.model.mappers.OrderMapper;
import com.example.code.model.modelUtils.OrderStatus;
import com.example.code.model.modelUtils.TimePeriod;
import com.example.code.repositories.OrderRepository;
import com.example.code.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryServiceLitRes implements DeliveryService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public DeliveryServiceLitRes(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<ResponseOrder> getOrders(UUID userId) throws UserNotFoundException {
        User user = getUserById(userId);
        return user.isCourier() ? getOrdersForCourier(user) : getOrdersForCustomer(user);
    }

    @Override
    public Order createOrder(int day, UUID userId) throws UserNotFoundException {
        User user = getUserById(userId);
        return createOrder(day, user);
    }

    @Override
    public List<TimePeriod> findAvailableTimePeriods(int orderId) throws OrderNotFoundException, IncorrectTimePeriodException {
        int day = getOrderDay(orderId);
        List<User> couriers = getCouriers();

        return findTimePeriodsForCouriers(couriers, day);
    }

    @Override
    public void cancelOrder(int orderId) throws OrderNotFoundException {
        Order order = getOrderFromDatabase(orderId);
        order.setCourier(null);
        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Override
    public void setTimeForOrder(int orderId, TimePeriod timePeriod) throws OrderNotFoundException {
        Order order = getOrderFromDatabase(orderId);
        order.setStartTime(timePeriod.getStart());
        order.setEndTime(timePeriod.getEnd());
        orderRepository.save(order);
    }

    @Override
    public void unsetTimeForOrder(int orderId) throws OrderNotFoundException {
        Order order = getOrderFromDatabase(orderId);
        order.setStartTime(0);
        order.setEndTime(0);
        order.setOrderStatus(OrderStatus.CREATED);
        orderRepository.save(order);
    }

    @Override
    public void choseCourierForOrder(int orderId) throws OrderNotFoundException, TimeIsNotAvailableException, IncorrectTimePeriodException, OrderHasBeenAlreadyAccepted {
        Order order = checkOrderNotAccepted(getOrderFromDatabase(orderId));
        List<User> fitCouriers = getAvailableCouriersForOrder(order);
        setCourierForOrder(fitCouriers, order);
    }

    @Override
    public ResponseOrder getOrder(int orderId) throws OrderNotFoundException {
        return OrderMapper.INSTANCE.toResponseOrders(getOrderFromDatabase(orderId));
    }

    @Override
    public void acceptOrder(int orderId) throws OrderNotFoundException, OrderHasBeenAlreadyAccepted {
        Order order = checkOrderNotAccepted(getOrderFromDatabase(orderId));
        order.setOrderStatus(OrderStatus.IN_PROCESS);
        orderRepository.save(order);
    }

    @Override
    public void completeOrder(int orderId) throws OrderNotFoundException, OrderHasntBeenAccepted {
        Order order = getOrderFromDatabase(orderId);
        if (order.isAccepted()) {
            order.setOrderStatus(OrderStatus.DONE);
            orderRepository.save(order);
        } else {
            throw new OrderHasntBeenAccepted();
        }
    }

    private Order checkOrderNotAccepted(Order order) throws OrderHasBeenAlreadyAccepted {
        if (order.isAccepted()) {
            throw new OrderHasBeenAlreadyAccepted();
        } else  {
            return order;
        }
    }

    private List<TimePeriod> findTimePeriodsForCouriers(List<User> couriers, int day) throws IncorrectTimePeriodException {
        return TimePeriod.createDefaultListOfTimePeriods().stream()
                .filter(timePeriod -> timePeriod.isAvailableForCouriersInThisDay(couriers, day))
                .collect(Collectors.toList());
    }

    private Order getOrderFromDatabase(int orderId) throws OrderNotFoundException {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new OrderNotFoundException();
        } else {
            return order.get();
        }
    }

    private List<ResponseOrder> getOrdersForCourier(User user) {
        return orderRepository.findAllByCourier(user.getId()).stream()
                .map(OrderMapper.INSTANCE::toResponseOrders)
                .collect(Collectors.toList());
    }

    private List<ResponseOrder> getOrdersForCustomer(User user) {
        return orderRepository.findAllByUser(user).stream()
                .map(OrderMapper.INSTANCE::toResponseOrders)
                .collect(Collectors.toList());
    }

    private void setCourierForOrder(List<User> fitCouriers, Order order) throws TimeIsNotAvailableException {
        if (fitCouriers.size() == 0) {
            throw new TimeIsNotAvailableException();
        } else {
            order.setCourier(fitCouriers.get(new Random().nextInt(fitCouriers.size())));
            order.setOrderStatus(OrderStatus.ON_APPROVE);
            orderRepository.save(order);
        }
    }

    private List<User> getAvailableCouriersForOrder(Order order) throws IncorrectTimePeriodException {
        TimePeriod timePeriod = new TimePeriod(order.getStartTime(), order.getEndTime());
        List<User> couriers = getCouriers();

        return couriers.stream()
                .filter(courier -> timePeriod.isAvailableForCourierInThisDay(courier, order.getDay()))
                .filter(courier -> order.getCourier() == null || !courier.getId().equals(order.getCourier().getId()))
                .collect(Collectors.toList());
    }

    private Order createOrder(int day, User user) {
        Order newOrder = new Order(day, OrderStatus.CREATED, user);
        orderRepository.save(newOrder);
        return newOrder;
    }

    private User getUserById(UUID userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private int getOrderDay(int orderId) throws OrderNotFoundException {
        return getOrderFromDatabase(orderId).getDay();
    }

    private List<User> getCouriers() {
        return userRepository.getAllByIsCourier(true);
    }
}
