package com.example.code.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false, name = "reserved_book_id")
    private Book reservedBook;

    @ManyToOne
    @JoinColumn(nullable = false, name = "order_number")
    private Order order;
}