package com.dormitory.backend.service;

import com.dormitory.backend.api.DormitoryRepository;
import com.dormitory.backend.api.DormitorySpecifications;
import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.dormitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
public class DormitoryService {
    @Autowired
    DormitoryRepository dormitoryRepository;
    public Page<dormitory> findByHouseNumAndFloorAndBuildingNameAndLocationByPage(
            String houseNum, Integer floor, String buildingName, String location,Integer page,Integer limit,String sort){
        Specification<dormitory> spec = DormitorySpecifications.findByCriteria(houseNum, floor, buildingName, location);
        if(page != null && limit != null){
            Sort sort_;
            if (sort==null||sort.equals("+")){
                sort_ = Sort.by("id").ascending();
            } else {
                sort_ = Sort.by("id").descending();
            }

            PageRequest pageable = PageRequest.of(page,limit,sort_);
            return dormitoryRepository.findAll(spec,pageable);
        }else{
            throw new MyException(Code.MISSING_FIELD);
        }
        // 调用 JpaRepository 的 findAll 方法，传入 Specification 对象
    }
    public List<dormitory> findByHouseNumAndFloorAndBuildingNameAndLocation(
            String houseNum, Integer floor, String buildingName, String location){
        Specification<dormitory> spec = DormitorySpecifications.findByCriteria(houseNum, floor, buildingName, location);
        // 调用 JpaRepository 的 findAll 方法，传入 Specification 对象
        return dormitoryRepository.findAll(spec);
    }
    public boolean checkRoomAvailable(String dormitoryId){
        dormitory dormitory = dormitoryRepository.findById(Integer.parseInt(dormitoryId));
        int bookedBed = dormitory.getBed();
        int bookedNumber = dormitory.getBookedNum();
        return bookedBed>bookedNumber;
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
