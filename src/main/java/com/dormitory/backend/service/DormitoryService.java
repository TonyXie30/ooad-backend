package com.dormitory.backend.service;

import com.dormitory.backend.api.DormitoryRepository;
import com.dormitory.backend.api.DormitorySpecifications;
import com.dormitory.backend.pojo.dormitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DormitoryService {
    @Autowired
    DormitoryRepository dormitoryRepository;
    public List<dormitory> findByHouseNumAndFloorAndBuildingNameAndLocation(String houseNum, Integer floor, String buildingName, String location){
        Specification<dormitory> spec = DormitorySpecifications.findByCriteria(houseNum, floor, buildingName, location);

        // 调用 JpaRepository 的 findAll 方法，传入 Specification 对象
        return dormitoryRepository.findAll(spec);
    }
    public boolean checkRoomAvailable(String dormitoryId){
        dormitory dormitory = dormitoryRepository.findById(Integer.parseInt(dormitoryId));
        return dormitoryRepository.findByBed(dormitory)>=dormitoryRepository.findByBookedNum(dormitory);
    }
    public List<String> findBuilding(String location){
        location = location==null?"":location;
        return dormitoryRepository.findBuilding("%"+location+"%");
    }
    public List<String> findFloor(String location,String buildingName){
        location = location==null?"":location;
        buildingName = buildingName==null?"":buildingName;
        return dormitoryRepository.findFloor("%"+location+"%","%"+buildingName+"%");
    }

    public dormitory findById(String dormitoryId){
        return dormitoryRepository.findById(Integer.parseInt(dormitoryId));
    }

    public dormitory checkRoomExisted(dormitory dormitory){
        return dormitoryRepository.findById(dormitory.getId());
    }

    public dormitory addDormitory(dormitory dormitory) {
        return dormitoryRepository.save(dormitory);
    }

    public void removeDormitory(dormitory dormitory) {
        dormitoryRepository.delete(dormitory);
    }
}
