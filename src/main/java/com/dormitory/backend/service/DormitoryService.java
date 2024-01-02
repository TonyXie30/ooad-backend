package com.dormitory.backend.service;

import com.dormitory.backend.api.*;
import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DormitoryService {
    @Autowired
    DormitoryRepository dormitoryRepository;
    @Autowired
    SelectionTimeConfigRepository selectionTimeConfigRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;

    public Page<Dormitory> findByHouseNumAndFloorAndBuildingNameAndLocationAndGenderAndDegree(
            String houseNum, Integer floor, String buildingName, String location,
            Gender gender, Degree degree,
            Integer page,Integer limit,String sort){
        Specification<Dormitory> spec = DormitorySpecifications
                .findByCriteria(houseNum, floor, buildingName, location, gender, degree);
        if(page != null && limit != null){
            Sort sort_ = Sort.by("location","buildingName","floor","houseNum");
            if (sort==null||sort.equals("+")){
                sort_ = sort_.ascending();
            } else {
                sort_ = sort_.descending();
            }

            PageRequest pageable = PageRequest.of(page,limit,sort_);
            return dormitoryRepository.findAll(spec,pageable);
        }else{
//            若强制调用该方法却缺少分页信息则抛出错误。不需要分页建议选择下方方法
            throw new MyException(Code.MISSING_FIELD);
        }
        // 调用 JpaRepository 的 findAll 方法，传入 Specification 对象
    }
    public Page<Dormitory> findByHouseNumAndFloorAndBuildingNameAndLocationAndGenderAndDegree(
            String houseNum, Integer floor, String buildingName, String location,
            Gender gender, Degree degree){
        Sort sort_ = Sort.by("location","buildingName","floor","houseNum").ascending();
        Specification<Dormitory> spec = DormitorySpecifications
                .findByCriteria(houseNum, floor, buildingName, location,gender,degree);
        // 调用 JpaRepository 的 findAll 方法，传入 Specification 对象
        return new PageImpl<>(dormitoryRepository.findAll(spec,sort_));
    }
    public boolean checkRoomAvailable(Integer dormitoryId){
        Dormitory dormitory = dormitoryRepository.findById(dormitoryId);
        int bookedBed = dormitory.getBed();
        int bookedNumber = dormitory.getBookedNum();
        return bookedBed>bookedNumber;
    }
    public List<String> findBuilding(String location){
        location = location==null?"":location;
        return dormitoryRepository.findBuilding(location);
    }
    public List<String> findFloor(String location,String buildingName){
        return dormitoryRepository.findFloor(
                location,
                buildingName);
    }

    public Dormitory findById(Integer dormitoryId){
        return dormitoryRepository.findById(dormitoryId);
    }

    public Dormitory checkRoomExisted(Dormitory dormitory){
        return dormitoryRepository.findById(dormitory.getId());
    }

    public Dormitory addDormitory(Dormitory dormitory) {
        return dormitoryRepository.save(dormitory);
    }

    public void removeDormitory(Dormitory dormitory) {
        dormitoryRepository.delete(dormitory);
    }

    public void setSelectionTime(SelectionTimeConfig config){
        selectionTimeConfigRepository.save(config);
    }
    public SelectionTimeConfig getSelectionTime(Gender gender, Degree degree){
        return selectionTimeConfigRepository.findByGenderAndDegree(gender,degree);
    }
    public List<SelectionTimeConfig> getSelectionTimeList(String gender, String degree){
        return selectionTimeConfigRepository.getSelectionTimeConfigListByGenderAndDegree(
                gender,
                degree);
    }
    public List<Comment> treeOfComments(Dormitory dormitory){
        return commentRepository.findFirstLevelComments(dormitory);
    }

    public void deleteSelectionTime(SelectionTimeConfig configInDB) {
        selectionTimeConfigRepository.delete(configInDB);
    }

    public void bookRoom(Dormitory dormitory) {
        bookRoom(dormitory,1);
    }
    public void bookRoom(Dormitory dormitory, int bookedNum) {
        dormitory.setBookedNum(dormitory.getBookedNum()+bookedNum);
        dormitoryRepository.save(dormitory);
    }

    public void checkOut(Dormitory dormitory) {
        dormitory.setBookedNum(dormitory.getBookedNum()-1);
        dormitoryRepository.save(dormitory);
    }

    public List<SelectionInfoExcelData> getSelectionInfo(String location, String buildingName,
                                                         String floor, String houseNum) {
        return dormitoryRepository.getSelectionInfoData(location, buildingName, String.valueOf(floor), houseNum);
    }
}
