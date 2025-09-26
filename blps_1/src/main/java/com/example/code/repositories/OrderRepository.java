package com.example.code.repositories;

import com.example.code.model.entities.Order;
import com.example.code.model.entities.User;
import com.example.code.model.modelUtils.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByCourier(UUID courierId);

    List<Order> findAllByUser(User user);
}
