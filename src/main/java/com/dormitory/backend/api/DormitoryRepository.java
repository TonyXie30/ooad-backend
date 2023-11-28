package com.dormitory.backend.api;

import com.dormitory.backend.pojo.dormitory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DormitoryRepository extends JpaRepository<dormitory, Long> {
    List<dormitory> findByHouseNumAndFloorAndBuildingNameAndLocation(String houseNum, Integer floor, String buildingName, String location);
    int findByBed(dormitory dormitory);
    int findByBookedNum(dormitory dormitory);
    dormitory findById(int id);
}
