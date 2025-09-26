package com.example.code.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_info")
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_courier", nullable = false)
    private boolean isCourier;

    @OneToMany(mappedBy = "user")
    private List<Order> ownOrders;

    @OneToMany(mappedBy = "courier")
    private List<Order> courierOrders;

    public List<Order> getOrdersFromCourierByDay(int day) {
        return this.courierOrders.stream()
                .filter(order -> order.getDay() == day)
                .collect(Collectors.toList());
    }
}
