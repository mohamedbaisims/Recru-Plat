package com.example.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = ?1")
    Long countByRole(Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'USER'")
    Long countCandidates();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'MANAGER'")
    Long countManagers();
}
