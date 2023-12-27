package com.dormitory.backend.api;

import com.dormitory.backend.pojo.Degree;
import com.dormitory.backend.pojo.Gender;
import com.dormitory.backend.pojo.UserProjection;
import com.dormitory.backend.pojo.user;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface UserRepository extends JpaRepository<user, Long> {
  user findByUsername(String username);
  user findById(int id);

  @Query("SELECT u from users u WHERE u.leaderId.id = :leaderId")
  List<user> findByLeaderId(Integer leaderId);
  @Query("SELECT u FROM users u JOIN u.bookmark b WHERE b.id = :dormitoryId")
  List<user> findByBookmarkedDormitoryId(@Param("dormitoryId") int dormitoryId);

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

  List<user> getByBookedDormitoryIsNotNull();

  @Query("SELECT u FROM users u JOIN u.bookedDormitory d WHERE d.id = :dormitoryId")
  List<user> findByCheckInedDormitoryId(@Param("dormitoryId") int dormitoryId);

  @Query(value="SELECT u FROM users u WHERE u.username != :#{#hostUser.username} and " +
          "u.gender= :#{#hostUser.gender} and u.degree = :#{#hostUser.degree}")
  Page<user> findPageFilterByUser(user hostUser,Pageable pageable);

  @Query(value="SELECT u FROM users u WHERE u.username != :#{#hostUser.username} and " +
          "u.gender= :#{#hostUser.gender} and u.degree = :#{#hostUser.degree}")
  List<user> findFilterByUser(user hostUser);
}

