package com.dormitory.backend.api;

import com.dormitory.backend.pojo.User;
import com.dormitory.backend.pojo.UserProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);
  User findById(int id);

  @Query("SELECT u from users u WHERE u.leaderId.id = :leaderId")
  List<User> findByLeaderId(Integer leaderId);
  @Query("SELECT u FROM users u JOIN u.bookmark b WHERE b.id = :dormitoryId")
  List<User> findByBookmarkedDormitoryId(@Param("dormitoryId") int dormitoryId);

  @Query(value = "SELECT user_id id,gender,degree,username,bedtime,uptime,name subject FROM " +
          "users join subject s on s.subject_id = users.subject_id " +
          "WHERE gender = :gender " +
          "AND degree = :degree AND users.username <> :excludeUsername " +
          "ORDER BY " +
          "ABS(EXTRACT(EPOCH FROM (bedtime - :targetBedtime))) " +
          "+ ABS(EXTRACT(EPOCH FROM (uptime - :targetUptime))) " +
          "LIMIT 10", nativeQuery = true)
  List<UserProjection> recommend(
          @Param("gender") String gender,
          @Param("degree") String degree,
          @Param("targetBedtime") LocalTime targetBedtime,
          @Param("targetUptime") LocalTime targetUptime,
          @Param("excludeUsername") String excludeUsername
  );

  List<User> getByBookedDormitoryIsNotNull();

  @Query("SELECT u FROM users u JOIN u.bookedDormitory d WHERE d.id = :dormitoryId")
  List<User> findByCheckInedDormitoryId(@Param("dormitoryId") int dormitoryId);

  @Query(value="SELECT u FROM users u WHERE u.username != :#{#hostUser.username} and " +
          "u.gender= :#{#hostUser.gender} and u.degree = :#{#hostUser.degree}")
  Page<User> findPageFilterByUser(User hostUser, Pageable pageable);

  @Query(value="SELECT u FROM users u WHERE u.username != :#{#hostUser.username} and " +
          "u.gender= :#{#hostUser.gender} and u.degree = :#{#hostUser.degree}")
  List<User> findFilterByUser(User hostUser);

  @Query(value = "DELETE FROM bookmark WHERE bookmark_dormitory_id = :id",nativeQuery = true)
  @Transactional
  @Modifying
    void deleteALlBookMarkByDorm(int id);
}

