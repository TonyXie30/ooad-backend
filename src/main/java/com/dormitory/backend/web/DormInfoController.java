package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.CommentResponseDTO;
import com.dormitory.backend.pojo.comment;
import com.dormitory.backend.pojo.dormitory;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.CommentService;
import com.dormitory.backend.service.DormitoryService;
import com.dormitory.backend.service.UserService;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.transaction.Transactional;
import org.springdoc.core.converters.models.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public boolean checkDormAvailableOrNot(@RequestParam String dormitoryId){
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
    public void checkInDorm(@RequestParam String dormitoryId, @RequestParam String username,
                            @JsonPropertyDescription(value = "若不指定则后端会查询当前系统时间")
                            @RequestParam(required = false) Date time){
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
            }
            else {
                throw new MyException(Code.Room_Occupied);
            }
        }
    }
    @PostMapping(value = "api/treeOfComments")
    @Transactional
    @ResponseBody
    public ResponseEntity<List<CommentResponseDTO>> allComment(@RequestParam String dormitory_id){
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
    @PostMapping(value = "api/checkTime")
    @ResponseBody
    public boolean checkTime(@RequestParam String username,
                             @JsonPropertyDescription(value = "若不指定则后端会查询当前系统时间")
                             @RequestParam(required = false) Date time){
        return checkTime_(username, time);
    }

//    模板方法
    public boolean checkTime_(String username, Date time){
        user user = userService.findByUsername(username);
        return checkTime_(user,time);
    }
    public boolean checkTime_(user user, Date time){
        if(time==null){
            time = new Date(new java.util.Date().getTime());
        }
        return userService.checkTimeValid(user, time);
    }

}
