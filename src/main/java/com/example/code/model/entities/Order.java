package com.example.code.model.entities;


import com.example.code.model.modelUtils.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order")
@Check(constraints = "day > 1 and day <= 31 " +
        "and start_time >= 0 and start_time <= 23 " +
        "and end_time >= 0 and end_time <= 23" +
        "and start_time < end_time")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int number;

    @Column(name = "order_status", nullable = false)
    @Enumerated
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private int day;

    @Column(name = "start_time")
    private int startTime;

    @Column(name = "end_time")
    private int endTime;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    private User courier;

    public Order(int day, OrderStatus orderStatus, User user) {
        this.day = day;
        this.orderStatus = orderStatus;
        this.user = user;
    }
}
