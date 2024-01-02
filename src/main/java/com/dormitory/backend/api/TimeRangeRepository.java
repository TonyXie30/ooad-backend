package com.dormitory.backend.api;

import com.dormitory.backend.pojo.TimeRange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;

public interface TimeRangeRepository extends JpaRepository<TimeRange, Long> {
  TimeRange findByTimeSlot(Time time);
  TimeRange findById(int id);
}

