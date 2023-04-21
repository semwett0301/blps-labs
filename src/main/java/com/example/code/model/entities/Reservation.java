package com.example.code.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @EmbeddedId
    private ReservationId reservationInfo;
}

class ReservationId implements Serializable {
    @ManyToOne
    private Book reservedBook;

    @ManyToOne
    private User user;
}
