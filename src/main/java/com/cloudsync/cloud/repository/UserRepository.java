package com.cloudsync.cloud.repository;

import com.cloudsync.cloud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User save(User user);
}
