package com.dormitory.backend.service;

import com.dormitory.backend.api.DormitoryRepository;
import com.dormitory.backend.pojo.dormitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DormitoryService {
    @Autowired
    DormitoryRepository dormitoryRepository;
    public List<dormitory> findByHouseNumAndFloorAndBuildingNameAndLocation(String houseNum, Integer floor, String buildingName, String location){
        return dormitoryRepository.findByHouseNumAndFloorAndBuildingNameAndLocation(houseNum,floor,buildingName,location);
    }
    public boolean checkRoomAvailable(dormitory dormitory){
        return dormitoryRepository.findByBed(dormitory)>=dormitoryRepository.findByBookedNum(dormitory);
    }
}
