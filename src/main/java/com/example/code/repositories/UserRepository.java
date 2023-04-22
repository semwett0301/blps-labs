package com.example.code.repositories;

import com.example.code.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> getAllByIsCourier(boolean courier);
}
