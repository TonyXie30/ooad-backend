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
    public Page<dormitory> findDorm(@RequestParam(required = false) String houseNum, @RequestParam(required = false) Integer floor,
                                    @RequestParam(required = false) String buildingName, @RequestParam(required = false) String location,
                                    @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit,
                                    @RequestParam(required = false) String sort
    ){
        if(page == null && limit == null){
            return dormitoryService.findByHouseNumAndFloorAndBuildingNameAndLocation(houseNum, floor, buildingName, location);
        }else{
            return dormitoryService.findByHouseNumAndFloorAndBuildingNameAndLocation(houseNum, floor, buildingName, location,page,limit,sort);
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
            return dormitoryService.checkRoomAvailable(dormitoryId);
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
            user user = userService.findByUsername(username);
            if (user.getLeaderId().getId() != user.getId()){
                throw new MyException(Code.UNAUTHORISED_NOT_LEADER);
            }
            if (dormitoryService.checkRoomAvailable(dormitoryId) && checkTime_(user,time)){
                dormitory dormitory = dormitoryService.findById(dormitoryId);
                userService.bookRoom(user,dormitory);
                dormitoryService.bookRoom(dormitory);
            }
            else {
                throw new MyException(Code.Room_Occupied);
            }
        }
    }
    @PostMapping(value = "api/treeOfComments")
    @Transactional
    @ResponseBody
    public ResponseEntity<List<CommentResponseDTO>> allComment(@RequestParam Integer dormitory_id){
        dormitory dorm = dormitoryService.findById(dormitory_id);
        List<comment> lst =dormitoryService.treeOfComments(dorm);
        List<CommentResponseDTO> commentDTOs = lst
                .stream()
                .map(commentId -> {
                    comment comment = commentService.findById(commentId.getId());
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
        user user = userService.findByUsername(username);
        return checkTime_(user,time);
    }
    public boolean checkTime_(user user, Timestamp time){
        if(time==null){
            time = new Timestamp(new java.util.Date().getTime());
        }
        return userService.checkTimeValid(user, time);
    }

}
