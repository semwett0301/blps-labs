package com.example.code.repositories;

import com.example.code.model.entities.Order;
import com.example.code.model.modelUtils.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
