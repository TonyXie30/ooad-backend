package com.dormitory.backend.api;

import com.dormitory.backend.pojo.dormitory;
import com.dormitory.backend.pojo.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<user, Long> {
  user findByUsername(String username);
  user findById(int id);
  List<user> findByLeaderId(int leaderId);
}

