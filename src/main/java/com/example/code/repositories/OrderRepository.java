package com.example.code.repositories;

import com.example.code.model.entities.Order;
import com.example.code.model.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByCourier(UserInfo userInfo);

    List<Order> findAllByUser(UserInfo userInfo);
}
