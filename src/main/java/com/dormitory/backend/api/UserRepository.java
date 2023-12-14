package com.dormitory.backend.api;

import com.dormitory.backend.pojo.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<user, Long> {
  user findByUsername(String username);
  user findById(int id);
  List<user> findByLeaderId(user leaderId);
  @Query("SELECT u FROM users u JOIN u.bookmark b WHERE b.id = :dormitoryId")
  List<user> findByBookmarkedDormitoryId(@Param("dormitoryId") int dormitoryId);

}

