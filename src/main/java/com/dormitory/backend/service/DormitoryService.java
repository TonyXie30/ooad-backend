package com.dormitory.backend.service;

import com.dormitory.backend.api.*;
import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    @Autowired
    GenderRepository genderRepository;
    @Autowired
    DegreeRepository degreeRepository;

    public Page<Dormitory> findByHouseNumAndFloorAndBuildingNameAndLocationAndGenderAndDegree(
            String houseNum, Integer floor, String buildingName, String location,
            Gender gender, Degree degree,
            Integer page, Integer limit, String sort) {
        Specification<Dormitory> spec = DormitorySpecifications
                .findByCriteria(houseNum, floor, buildingName, location, gender, degree);
        if (page != null && limit != null) {
            Sort sort_ = Sort.by("location", "buildingName", "floor", "houseNum");
            if (sort == null || sort.equals("+")) {
                sort_ = sort_.ascending();
            } else {
                sort_ = sort_.descending();
            }

            PageRequest pageable = PageRequest.of(page, limit, sort_);
            return dormitoryRepository.findAll(spec, pageable);
        } else {
//            若强制调用该方法却缺少分页信息则抛出错误。不需要分页建议选择下方方法
            throw new MyException(Code.MISSING_FIELD);
        }
        // 调用 JpaRepository 的 findAll 方法，传入 Specification 对象
    }

    @Cacheable(cacheNames = "fetchByCompositeKey", cacheManager = "stringKeyCacheManager", keyGenerator = "dormKeyGenerator")
    public Page<Dormitory> findByHouseNumAndFloorAndBuildingNameAndLocationAndGenderAndDegree(
            String houseNum, Integer floor, String buildingName, String location,
            Gender gender, Degree degree) {
        return findByHouseNumAndFloorAndBuildingNameAndLocationAndGenderAndDegree_(
                houseNum, floor, buildingName, location, gender, degree);
    }

    public Page<Dormitory> findByHouseNumAndFloorAndBuildingNameAndLocationAndGenderAndDegree_(
            String houseNum, Integer floor, String buildingName, String location,
            Gender gender, Degree degree) {
        Sort sort_ = Sort.by("location", "buildingName", "floor", "houseNum").ascending();
        Specification<Dormitory> spec = DormitorySpecifications
                .findByCriteria(houseNum, floor, buildingName, location, gender, degree);
        // 调用 JpaRepository 的 findAll 方法，传入 Specification 对象
        return new PageImpl<>(dormitoryRepository.findAll(spec, sort_));
    }

    public List<Dormitory> findByHouseNumAndFloorAndBuildingNameAndLocationAndGenderAndDegree(
            String houseNum, String floor, String buildingName, String location,
            String gender, String degree) {
        Sort sort_ = Sort.by("location", "buildingName", "floor", "houseNum").ascending();
        Specification<Dormitory> spec = DormitorySpecifications
                .findByCriteria(houseNum, Integer.parseInt(floor), buildingName,
                        location, genderRepository.findByGender(gender), degreeRepository.findByDegree(degree));
        return dormitoryRepository.findAll(spec, sort_);

    }

    public boolean checkRoomAvailable(Integer dormitoryId, Integer bookNum) {
        Dormitory dormitory = dormitoryRepository.findById(dormitoryId);
        int bookedBed = dormitory.getBed();
        int bookedNumber = dormitory.getBookedNum();
        return bookedBed > bookedNumber + bookNum;
    }

    public List<String> findBuilding(String location) {
        location = location == null ? "" : location;
        return dormitoryRepository.findBuilding(location);
    }

    public List<String> findFloor(String location, String buildingName) {
        return dormitoryRepository.findFloor(
                location,
                buildingName);
    }

    @Cacheable(cacheNames = "fetchById", cacheManager = "dormCacheManager", key = "#dormitoryId")
    public Dormitory findById(Integer dormitoryId) {
        return dormitoryRepository.findById(dormitoryId);
    }

    public Dormitory checkRoomExisted(Dormitory Dormitory) {
        return dormitoryRepository.findById(Dormitory.getId());
    }

    public Dormitory addDormitory(Dormitory Dormitory) {
        return dormitoryRepository.save(Dormitory);
    }

    public void removeDormitory(Dormitory dormitory) {
        List<Comment> comments = commentRepository.findByDormitory(dormitory);
        commentRepository.deleteAllInBatch(comments);

        List<User> users = userRepository.findByCheckInedDormitoryId(dormitory.getId());
        for (User user: users) {
            user.setBookedDormitory(null);
            userRepository.save(user);
        }

        userRepository.deleteALlBookMarkByDorm(dormitory.getId());

        dormitoryRepository.delete(dormitory);
    }

    public void setSelectionTime(SelectionTimeConfig config) {
        selectionTimeConfigRepository.save(config);
    }

    public SelectionTimeConfig getSelectionTime(Gender gender, Degree degree) {
        return selectionTimeConfigRepository.findByGenderAndDegree(gender, degree);
    }

    public List<SelectionTimeConfig> getSelectionTimeList(String gender, String degree) {
        return selectionTimeConfigRepository.getSelectionTimeConfigListByGenderAndDegree(
                gender,
                degree);
    }

    public List<Comment> treeOfComments(Dormitory Dormitory) {
        return commentRepository.findFirstLevelComments(Dormitory);
    }

    public void deleteSelectionTime(SelectionTimeConfig configInDB) {
        selectionTimeConfigRepository.delete(configInDB);
    }

    public void bookRoom(Dormitory Dormitory) {
        bookRoom(Dormitory, 1);
    }

    public void bookRoom(Dormitory Dormitory, int bookedNum) {
        Dormitory.setBookedNum(Dormitory.getBookedNum() + bookedNum);
        dormitoryRepository.save(Dormitory);
    }

    public void checkOut(Dormitory Dormitory) {
        Dormitory.setBookedNum(Dormitory.getBookedNum() - 1);
        dormitoryRepository.save(Dormitory);
    }

    public List<SelectionInfoExcelData> getSelectionInfo(String location, String buildingName,
                                                         String floor, String houseNum) {
        return dormitoryRepository.getSelectionInfoData(location, buildingName, String.valueOf(floor), houseNum);
    }
}
