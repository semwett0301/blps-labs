package com.example.code.model.entities;


import com.example.code.model.exceptions.OrderHasBeenAlreadyAcceptedException;
import com.example.code.model.exceptions.TimeHasBeenAlreadyChosenException;
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
@Table(name = "order_book")
@Check(constraints = "day > 1 and day <= 31 " +
        "and start_time >= 0 and start_time <= 23 " +
        "and end_time >= 0 and end_time <= 23 " +
        "and start_time <= end_time")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int number;

    @Column(name = "order_status", nullable = false)
    @Enumerated
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private int day;

    @Column(name = "start_time")
    private Integer startTime;

    @Column(name = "end_time")
    private Integer endTime;

    @ManyToOne
    @JoinColumn(nullable = false)
    private UserInfo user;

    @ManyToOne
    private UserInfo courier;

    public Order(int day, OrderStatus orderStatus, UserInfo user) {
        this.day = day;
        this.orderStatus = orderStatus;
        this.user = user;
    }

    public Order validateNotAccepted() throws OrderHasBeenAlreadyAcceptedException {
        if (orderStatus == OrderStatus.IN_PROCESS || orderStatus == OrderStatus.DONE) {
            throw new OrderHasBeenAlreadyAcceptedException();
        }

        return this;
    }

    public Order validateTimeNotSet() throws TimeHasBeenAlreadyChosenException {
        if (startTime != null || endTime != null) {
            throw new TimeHasBeenAlreadyChosenException();
        }

        return this;
    }
}
