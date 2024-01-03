package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.*;
import com.dormitory.backend.service.CommentService;
import com.dormitory.backend.service.DormitoryService;
import com.dormitory.backend.service.UserService;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class DormInfoController {
    @Autowired
    private DormitoryService dormitoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    @CrossOrigin
    @PostMapping(value = "api/findDorm")
    @ResponseBody
    public Page<Dormitory> findDorm(@RequestParam(required = false) String houseNum, @RequestParam(required = false) Integer floor,
                                    @RequestParam(required = false) String buildingName, @RequestParam(required = false) String location,
                                    @RequestParam(required = false) String username,
                                    @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit,
                                    @RequestParam(required = false) String sort
    ){
        return findDorm_(houseNum, floor, buildingName, location, username, page, limit, sort);
    }
    public Page<Dormitory> findDorm_(String houseNum, Integer floor,
                                     String buildingName, String location,
                                     String username,
                                     Integer page, Integer limit,
                                     String sort
    ){
        Gender gender = null;
        Degree degree = null;
        if(username!=null) {
            User user = userService.findByUsername(username);
            if (user != null){
                gender = user.getGender();
                degree = user.getDegree();
            }
        }
        if(floor==null){
            floor=-1;
        }
        if(page == null && limit == null){
            return dormitoryService
                    .findByHouseNumAndFloorAndBuildingNameAndLocationAndGenderAndDegree(houseNum, floor, buildingName, location, gender, degree);
        }else{
            return dormitoryService
                    .findByHouseNumAndFloorAndBuildingNameAndLocationAndGenderAndDegree(houseNum, floor, buildingName, location, gender, degree, page,limit,sort);
        }
    }
    @CrossOrigin
    @PostMapping(value = "api/findBuilding")
    @ResponseBody
    public List<String> findBuilding(@RequestParam(required = false) String location){
        return dormitoryService.findBuilding(location);
    }
    @CrossOrigin
    @PostMapping(value = "api/findFloor")
    @ResponseBody
    public List<String> findFloor(@RequestParam(required = false) String location,
                                  @RequestParam(required = false) String buildingName){
        return dormitoryService.findFloor(location,buildingName);
    }
    @CrossOrigin
    @PostMapping(value = "api/checkDorm")
    @ResponseBody
    public boolean checkDormAvailableOrNot(@RequestParam Integer dormitoryId){
        if (dormitoryId==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        else{
            return dormitoryService.checkRoomAvailable(dormitoryId, 0);
        }
    }

    @CrossOrigin
    @PostMapping(value = "api/checkInDorm")
    @ResponseBody
    public void checkInDorm(@RequestParam Integer dormitoryId, @RequestParam String username,
                            @JsonPropertyDescription(value = "若不指定则后端会查询当前系统时间")
                            @RequestParam(required = false) Timestamp time){
        if (dormitoryId==null||username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        else{
            User user = userService.findByUsername(username);
            if (user.getLeaderId().getId() != user.getId()){
                throw new MyException(Code.UNAUTHORISED_NOT_LEADER);
            }
            Dormitory dormitory = dormitoryService.findById(dormitoryId);
            if (dormitory==null) throw new MyException(Code.DORMITORY_NOT_EXIST);
            int bookNum = Math.max(userService.findByLeaderId(user).size(), 1);
            if (dormitoryService.checkRoomAvailable(dormitoryId,bookNum) && checkTime_(user,time)){
                dormitoryService.bookRoom(dormitory,bookNum);
                userService.bookRoom(user,dormitory);
                userService.disbandTeam(userService.findByUsername(username));
            }
            else {
                throw new MyException(Code.Room_Occupied);
            }
        }
    }

    @PostMapping(value = "api/checkOutDorm")
    @ResponseBody
    public void checkOutDorm(@RequestParam String username,@RequestParam Integer dormitoryid){
        User user = userService.findByUsername(username);
        Dormitory dormitory = dormitoryService.findById(dormitoryid);
        if(dormitory == null || user == null){
            throw new MyException(Code.GENERAL_NOT_EXIST);
        }
        if(user.getBookedDormitory().getId()!=dormitoryid){
            throw new MyException(Code.NOT_BOOKED_DORMITORY);
        }
        dormitoryService.checkOut(dormitory);
        userService.checkOut(user);
    }

    @PostMapping(value = "api/treeOfComments")
    @Transactional
    @ResponseBody
    public ResponseEntity<List<CommentResponseDTO>> allComment(@RequestParam Integer dormitory_id){
        Dormitory dorm = dormitoryService.findById(dormitory_id);
        List<Comment> lst =dormitoryService.treeOfComments(dorm);
        List<CommentResponseDTO> commentDTOs = lst
                .stream()
                .map(commentId -> {
                    Comment comment = commentService.findById(commentId.getId());
                    return comment != null ? commentService.convertToDTO(comment) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentDTOs);
    }

    @CrossOrigin
    @PostMapping(value = "api/getSelectionTime")
    @ResponseBody
    public List<SelectionTimeConfig> getSelectionTime(@RequestParam(required = false) String gender,
                                                      @RequestParam(required = false) String degree){
        if (gender==null){
            gender="";
        }
        if (degree==null){
            degree="";
        }
        List<SelectionTimeConfig> configInDB = dormitoryService.getSelectionTimeList(gender,degree);
        if (configInDB==null){
            throw new MyException(Code.GENERAL_NOT_EXIST);
        }
        return configInDB;
    }

    @CrossOrigin
    @PostMapping(value = "api/checkTime")
    @ResponseBody
    public boolean checkTime(@RequestParam String username,
                             @JsonPropertyDescription(value = "若不指定则后端会查询当前系统时间")
                             @RequestParam(required = false) Timestamp time){
        return checkTime_(username, time);
    }

//    模板方法
    public boolean checkTime_(String username, Timestamp time){
        User user = userService.findByUsername(username);
        return checkTime_(user,time);
    }
    public boolean checkTime_(User user, Timestamp time){
        if(time==null){
            time = new Timestamp(new java.util.Date().getTime());
        }
        return userService.checkTimeValid(user, time);
    }

}
