package com.example.mailservice.repositories;

import com.example.mailservice.model.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, UUID> {
    Optional<UserInfo> findByUsername(String username);
}
