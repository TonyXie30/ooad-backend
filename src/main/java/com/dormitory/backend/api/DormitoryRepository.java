package com.dormitory.backend.api;

import com.dormitory.backend.pojo.SelectionInfoExcelData;
import com.dormitory.backend.pojo.dormitory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
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

//    这是另一种返回值映射的方式
//    注意要将string_agg的类型显式转换为string，否则只能用Object接收
    @Query(value = "SELECT new com.dormitory.backend.pojo.SelectionInfoExcelData(" +
            "d.location,d.buildingName ,d.floor, d.houseNum, " +
            "d.gender.gender ,d.degree.degree , d.bookedNum ,d.bed , " +
            "cast( string_agg(u.username,',') as string )) from dormitory d, users u " +
            "WHERE d.id = u.bookedDormitory.id and (d.location = :location or :location = '') and " +
            "(d.buildingName = :buildingName or :buildingName = '') and " +
            "(cast(d.floor as string) = :floor or :floor = '') and " +
            "(d.houseNum = :houseNum or :houseNum = '') " +
            "GROUP BY d.bed, d.bookedNum, d.buildingName, " +
            "         d.floor, d.houseNum, d.location, d.degree, d.gender")
    List<SelectionInfoExcelData> getSelectionInfoData(String location, String buildingName,
                                                      String floor, String houseNum);

/*
SELECT d.bed, d.booked_num, d.building_name,
       d.floor, d.house_num, d.location, d.degree,
       d.gender, array_agg(username) from dormitory d
JOIN users u on d.dormitory_id = u.dormitory_id
GROUP BY d.bed, d.booked_num, d.building_name,
         d.floor, d.house_num, d.location, d.degree, d.gender
 */
//    List<dormitoryUserInfo> export();
}
