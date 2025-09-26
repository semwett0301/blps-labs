package com.example.code.repositories;

import com.example.code.model.entities.UserInfo;
import com.example.code.model.modelUtils.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<UserInfo, UUID> {
    List<UserInfo> getAllByRole(Role role);
    Optional<UserInfo> findByUsername(String username);
}
