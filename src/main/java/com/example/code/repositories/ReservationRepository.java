package com.example.code.repositories;

import com.example.code.model.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findAllByOrderNumber(Integer orderNumber);
}
