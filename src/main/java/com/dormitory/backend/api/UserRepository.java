package com.dormitory.backend.api;

import com.dormitory.backend.pojo.users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<users, Long> {
  users findByUsername(String username);
}

