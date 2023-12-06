package com.dormitory.backend.api;

import com.dormitory.backend.pojo.dormitory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface DormitoryRepository extends JpaRepository<dormitory, Long>, JpaSpecificationExecutor<dormitory> {
    dormitory findById(int id);
    @Query("select DISTINCT d.buildingName from dormitory d " +
            "where (d.location like :location)" +
            "order by d.buildingName asc")
    List<String> findBuilding(String location);
    @Query("select DISTINCT d.floor from dormitory d " +
            "where (d.location like :location and d.buildingName like :buildingName)" +
            "order by d.floor asc")
    List<String> findFloor(String location,String buildingName);
}
