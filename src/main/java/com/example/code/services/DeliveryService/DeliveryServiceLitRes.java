package com.example.code.services.DeliveryService;

import com.example.code.model.dto.response.ResponseOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.entities.UserInfo;
import com.example.code.model.exceptions.*;
import com.example.code.model.mappers.OrderMapper;
import com.example.code.model.modelUtils.OrderStatus;
import com.example.code.model.modelUtils.Role;
import com.example.code.model.modelUtils.TimePeriod;
import com.example.code.repositories.OrderRepository;
import com.example.code.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public List<ResponseOrder> getOrders(String username) throws UserNotFoundException {
        UserInfo user = getUserByUsername(username);
        return user.getRole().equals(Role.COURIER) ? getOrdersForCourier(user) : getOrdersForCustomer(user);
    }

    @Override
    public Order createOrder(int day, String username) throws UserNotFoundException {
        UserInfo user = getUserByUsername(username);
        return createOrder(day, user);
    }

    @Override
    public List<TimePeriod> findAvailableTimePeriods(int orderId) throws OrderNotFoundException, IncorrectTimePeriodException {
        int day = getOrderDay(orderId);
        List<UserInfo> couriers = getCouriers();

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
    @Transactional
    public void setTimeForOrder(int orderId, TimePeriod timePeriod) throws OrderNotFoundException, TimeIsNotAvailableException, IncorrectTimePeriodException, OrderHasBeenAlreadyAcceptedException {
        Order order = getOrderFromDatabase(orderId);
        order.setStartTime(timePeriod.getStart());
        order.setEndTime(timePeriod.getEnd());
        orderRepository.save(order);

        choseCourierForOrder(orderId);
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
    @Transactional
    public void choseCourierForOrder(int orderId) throws OrderNotFoundException, TimeIsNotAvailableException, IncorrectTimePeriodException, OrderHasBeenAlreadyAcceptedException {
        Order order = checkOrderNotAccepted(getOrderFromDatabase(orderId));
        List<UserInfo> fitCouriers = getAvailableCouriersForOrder(order);
        setCourierForOrder(fitCouriers, order);
    }

    @Override
    public ResponseOrder getOrder(int orderId) throws OrderNotFoundException {
        return OrderMapper.INSTANCE.toResponseOrders(getOrderFromDatabase(orderId));
    }

    @Override
    public void acceptOrder(int orderId) throws OrderNotFoundException, OrderHasBeenAlreadyAcceptedException {
        Order order = checkOrderNotAccepted(getOrderFromDatabase(orderId));
        order.setOrderStatus(OrderStatus.IN_PROCESS);
        orderRepository.save(order);
    }

    @Override
    public void completeOrder(int orderId) throws OrderNotFoundException {
        Order order = getOrderFromDatabase(orderId);
        order.setOrderStatus(OrderStatus.DONE);
        orderRepository.save(order);
    }

    private Order checkOrderNotAccepted(Order order) throws OrderHasBeenAlreadyAcceptedException {
        if (order.isAccepted()) {
            throw new OrderHasBeenAlreadyAcceptedException();
        } else {
            return order;
        }
    }

    private List<TimePeriod> findTimePeriodsForCouriers(List<UserInfo> couriers, int day) throws IncorrectTimePeriodException {
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

    private List<ResponseOrder> getOrdersForCourier(UserInfo user) {
        return orderRepository.findAllByCourier(user.getId()).stream()
                .map(OrderMapper.INSTANCE::toResponseOrders)
                .collect(Collectors.toList());
    }

    private List<ResponseOrder> getOrdersForCustomer(UserInfo user) {
        return orderRepository.findAllByUser(user).stream()
                .map(OrderMapper.INSTANCE::toResponseOrders)
                .collect(Collectors.toList());
    }

    public void setCourierForOrder(List<UserInfo> fitCouriers, Order order) throws TimeIsNotAvailableException {
        if (fitCouriers.size() == 0) {
            throw new TimeIsNotAvailableException();
        } else {
            order.setCourier(fitCouriers.get(new Random().nextInt(fitCouriers.size())));
            order.setOrderStatus(OrderStatus.ON_APPROVE);
            orderRepository.save(order);
        }
    }

    private List<UserInfo> getAvailableCouriersForOrder(Order order) throws IncorrectTimePeriodException {
        TimePeriod timePeriod = new TimePeriod(order.getStartTime(), order.getEndTime());
        List<UserInfo> couriers = getCouriers();

        return couriers.stream()
                .filter(courier -> timePeriod.isAvailableForCourierInThisDay(courier, order.getDay()))
                .filter(courier -> !courier.equals(order.getCourier()))
                .collect(Collectors.toList());
    }

    private Order createOrder(int day, UserInfo user) {
        Order newOrder = new Order(day, OrderStatus.CREATED, user);
        orderRepository.save(newOrder);
        return newOrder;
    }

    private UserInfo getUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    private int getOrderDay(int orderId) throws OrderNotFoundException {
        return getOrderFromDatabase(orderId).getDay();
    }

    private List<UserInfo> getCouriers() {
        return userRepository.getAllByRole(Role.COURIER);
    }
}
