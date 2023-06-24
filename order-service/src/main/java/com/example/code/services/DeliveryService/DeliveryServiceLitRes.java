package com.example.code.services.DeliveryService;

import com.example.code.model.dto.kafka.OrderDTO;
import com.example.code.model.dto.web.response.ResponseOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.entities.UserInfo;
import com.example.code.model.exceptions.*;
import com.example.code.model.mappers.OrderMapper;
import com.example.code.model.modelUtils.OrderStatus;
import com.example.code.model.modelUtils.ReservedBook;
import com.example.code.model.modelUtils.Role;
import com.example.code.model.modelUtils.TimePeriod;
import com.example.code.repositories.OrderRepository;
import com.example.code.repositories.UserRepository;
import com.example.code.services.WarehouseService.WarehouseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class DeliveryServiceLitRes implements DeliveryService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private final WarehouseService warehouseService;

    @Autowired
    public DeliveryServiceLitRes(UserRepository userRepository, OrderRepository orderRepository, WarehouseService warehouseService, Producer<String, OrderDTO> producer) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.warehouseService = warehouseService;
    }

    @Override
    public List<ResponseOrder> getOrders(String username) throws UserNotFoundException {
        UserInfo user = getUserByUsername(username);
        return user.getRole().equals(Role.COURIER) ? getOrdersForCourier(user) : getOrdersForCustomer(user);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Order createOrder(int day, List<ReservedBook> books, String username) throws UserNotFoundException, BookIsNotAvailableException, JsonProcessingException {
        UserInfo user = getUserByUsername(username);
        Order order = createOrder(day, user);
        warehouseService.reserveBooks(books, order);
        sendOrder(order);
        return order;
    }

    @Override
    public List<TimePeriod> findAvailableTimePeriods(int orderId) throws OrderNotFoundException, IncorrectTimePeriodException {
        int day = getOrderDay(orderId);
        List<UserInfo> couriers = getCouriers();

        return findTimePeriodsForCouriers(couriers, day);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void cancelOrder(int orderId) throws OrderNotFoundException, JsonProcessingException {
        Order order = getOrderSafety(orderId);
        saveOrder(order, OrderStatus.CANCELED, null);
        warehouseService.removeReservation(orderId);
        sendOrder(order);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void setTimeForOrder(int orderId, TimePeriod timePeriod) throws OrderNotFoundException, TimeIsNotAvailableException, IncorrectTimePeriodException, OrderHasBeenAlreadyAcceptedException, TimeHasBeenAlreadyChosenException {
        setStartAndEndTimeForOrder(orderId, timePeriod);
        choseCourierForOrder(orderId);
    }

    @Override
    public void choseCourierForOrder(int orderId) throws OrderNotFoundException, TimeIsNotAvailableException, IncorrectTimePeriodException, OrderHasBeenAlreadyAcceptedException {
        try {
            findAndSetCourierForOrder(orderId);
        } catch (TimeIsNotAvailableException ex) {
            unsetTimeForOrder(orderId);
            throw ex;
        }
    }

    @Override
    public ResponseOrder getOrder(int orderId) throws OrderNotFoundException {
        return OrderMapper.INSTANCE.toResponseOrders(getOrderSafety(orderId));
    }

    @Override
    public void acceptOrder(int orderId) throws OrderNotFoundException, OrderHasBeenAlreadyAcceptedException, JsonProcessingException {
        Order order = getOrderSafety(orderId);
        saveOrder(order.validateNotAccepted(), OrderStatus.IN_PROCESS);
        sendOrder(order);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void completeOrder(int orderId) throws OrderNotFoundException, JsonProcessingException {
        Order order = getOrderSafety(orderId);
        saveOrder(getOrderSafety(orderId), OrderStatus.DONE);
        warehouseService.removeReservation(orderId);
        sendOrder(order);
    }

    private OrderDTO getOrderDTO(Order order) {
        OrderDTO orderDTO = OrderMapper.INSTANCE.toOrderDTO(order);
        orderDTO.setUsername((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return orderDTO;
    }

    public Map<Integer, Order> ordersMap = new HashMap<>();

    private void sendOrder(Order order) {
        ordersMap.put(order.getNumber(), order);
    }

    private void saveOrder(Order order, OrderStatus orderStatus) {
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
    }

    private void saveOrder(Order order, OrderStatus orderStatus, @Nullable UserInfo courier) {
        order.setOrderStatus(orderStatus);
        order.setCourier(courier);
        orderRepository.save(order);
    }

    private void findAndSetCourierForOrder(int orderId) throws OrderNotFoundException, OrderHasBeenAlreadyAcceptedException, IncorrectTimePeriodException, TimeIsNotAvailableException {
        Order order = getOrderSafety(orderId).validateNotAccepted();
        List<UserInfo> fitCouriers = getAvailableCouriersForOrder(order);
        setCourierForOrderSafety(fitCouriers, order);
    }

    private void unsetTimeForOrder(int orderId) throws OrderNotFoundException {
        Order order = getOrderSafety(orderId);
        order.setStartTime(0);
        order.setEndTime(0);
        order.setOrderStatus(OrderStatus.CREATED);
        orderRepository.save(order);
    }

    private void setStartAndEndTimeForOrder(int orderId, TimePeriod timePeriod) throws OrderNotFoundException, TimeHasBeenAlreadyChosenException {
        Order order = getOrderSafety(orderId).validateTimeNotSet();
        order.setStartTime(timePeriod.getStart());
        order.setEndTime(timePeriod.getEnd());
        orderRepository.save(order);
    }

    private List<TimePeriod> findTimePeriodsForCouriers(List<UserInfo> couriers, int day) throws IncorrectTimePeriodException {
        return TimePeriod.createDefaultListOfTimePeriods().stream()
                .filter(timePeriod -> timePeriod.isAvailableForCouriersInThisDay(couriers, day))
                .collect(Collectors.toList());
    }

    private Order getOrderSafety(int orderId) throws OrderNotFoundException {
        return orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
    }

    private List<ResponseOrder> getOrdersForCourier(UserInfo user) {
        return orderRepository.findAllByCourier(user).stream()
                .map(OrderMapper.INSTANCE::toResponseOrders)
                .collect(Collectors.toList());
    }

    private List<ResponseOrder> getOrdersForCustomer(UserInfo user) {
        return orderRepository.findAllByUser(user).stream()
                .map(OrderMapper.INSTANCE::toResponseOrders)
                .collect(Collectors.toList());
    }

    private void setCourierForOrderSafety(List<UserInfo> fitCouriers, Order order) throws TimeIsNotAvailableException {
        if (fitCouriers.size() == 0) {
            throw new TimeIsNotAvailableException();
        } else {
            setCourierForOrder(fitCouriers, order);
        }
    }

    private void setCourierForOrder(List<UserInfo> fitCouriers, Order order) {
        order.setCourier(fitCouriers.get(new Random().nextInt(fitCouriers.size())));
        order.setOrderStatus(OrderStatus.ON_APPROVE);
        orderRepository.save(order);
        sendOrder(order);
    }

    private List<UserInfo> getAvailableCouriersForOrder(Order order) throws IncorrectTimePeriodException {
        TimePeriod timePeriod = new TimePeriod(order.getStartTime(), order.getEndTime());
        List<UserInfo> couriers = getCouriers();

        return couriers.stream()
                .filter(courier -> timePeriod.isAvailableForCourierInThisDay(courier, order.getDay()))
                .filter(courier -> order.getCourier() == null || !courier.getUsername().equals(order.getCourier().getUsername()))
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
        return getOrderSafety(orderId).getDay();
    }

    private List<UserInfo> getCouriers() {
        return userRepository.getAllByRole(Role.COURIER);
    }
}
