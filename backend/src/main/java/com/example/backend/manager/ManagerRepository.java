package com.example.backend.manager;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<User, Integer> {
}
