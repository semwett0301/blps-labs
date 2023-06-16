package com.example.code.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
@Check(constraints = "min_age >= 0 and price >= 0")
public class Book extends BaseEntity{
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    private String description;

    @Column(name = "min_age", nullable = false)
    private int minimumAge;

    @Column(name = "available_amount", nullable = false)
    private int availableAmount;

    @OneToMany(mappedBy = "reservedBook")
    private List<Reservation> reservations;

    public boolean isAvailable(int requestedAmount) {
        return availableAmount > requestedAmount;
    }
}
