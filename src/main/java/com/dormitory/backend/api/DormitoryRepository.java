package com.dormitory.backend.api;

import com.dormitory.backend.pojo.dormitory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface DormitoryRepository extends JpaRepository<dormitory, Long>, JpaSpecificationExecutor<dormitory> {
    dormitory findById(int id);

//    留意这里处理空字段的逻辑。字段 = 输入 or 输入 = 空 的逻辑可以完美实现若该输入为空则无视这个字段的需求
    @Query("select DISTINCT d.buildingName from dormitory d " +
            "where (d.location = :location or :location = '')" +
            "order by d.buildingName asc")
    List<String> findBuilding(String location);
    @Query("select DISTINCT d.floor from dormitory d " +
            "where ((d.location = :location or :location = '') " +
            "and d.buildingName = :buildingName or :buildingName = '')" +
            "order by d.floor asc")
    List<String> findFloor(String location,String buildingName);
}
