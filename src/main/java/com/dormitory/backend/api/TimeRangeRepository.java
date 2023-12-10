package com.dormitory.backend.api;

import com.dormitory.backend.pojo.timeRange;
import com.dormitory.backend.pojo.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.util.List;

public interface TimeRangeRepository extends JpaRepository<timeRange, Long> {
  timeRange findByTimeSlot(Time time);
  timeRange findById(int id);
}

