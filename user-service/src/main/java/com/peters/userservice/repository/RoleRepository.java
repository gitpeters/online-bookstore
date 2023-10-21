package com.peters.userservice.repository;

import com.peters.userservice.entity.User;
import com.peters.userservice.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByName(String name);

    List<UserRole> findByUsers(User user);
}
