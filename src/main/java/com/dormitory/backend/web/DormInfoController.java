package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.dormitory;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.DormitoryService;
import com.dormitory.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DormInfoController {
    @Autowired
    private DormitoryService dormitoryService;
    @Autowired
    private UserService userService;
    @CrossOrigin
    @PostMapping(value = "api/findDorm")
    @ResponseBody
    public List<dormitory> findDorm(@RequestBody String houseNum, Integer floor, String buildingName, String location){
        if (houseNum==null&&floor==null&&buildingName==null&&location==null)
            throw new MyException(Code.MISSING_FIELD);
        else {
            return dormitoryService.findByHouseNumAndFloorAndBuildingNameAndLocation(houseNum, floor, buildingName, location);
        }
    }
    @CrossOrigin
    @PostMapping(value = "api/checkDorm")
    @ResponseBody
    public boolean checkDormAvailableOrNot(@RequestBody dormitory dormitory){
        if (dormitory==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        else{
            return dormitoryService.checkRoomAvailable(dormitory);
        }
    }

    @CrossOrigin
    @PostMapping(value = "api/checkInDorm")
    @ResponseBody
    public void checkInDorm(@RequestBody dormitory dormitory, user user){
        if (dormitory==null||user==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        else{
            if (user.getLeaderId().getId() != user.getId()){
                throw new MyException(Code.UNAUTHORISED_NOT_LEADER);
            }
            if (dormitoryService.checkRoomAvailable(dormitory)){
                userService.bookRoom(user,dormitory);
            }
            else {
                throw new MyException(Code.Room_Occupied);
            }
        }
    }
}
