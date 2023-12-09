package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.dormitory;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.DormitoryService;
import com.dormitory.backend.service.UserService;
import org.springdoc.core.converters.models.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<dormitory> findDorm(@RequestParam(required = false) String houseNum, @RequestParam(required = false) Integer floor,
                                    @RequestParam(required = false) String buildingName, @RequestParam(required = false) String location,
                                    @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit,
                                    @RequestParam(required = false) String sort
    ){
        if (page!=null && limit!=null){
            return dormitoryService.findByHouseNumAndFloorAndBuildingNameAndLocationByPage(houseNum, floor, buildingName, location, page, limit, sort);
        }
        List<dormitory> nonPaged = dormitoryService.findByHouseNumAndFloorAndBuildingNameAndLocation(houseNum, floor, buildingName, location);
        return new PageImpl<>(nonPaged);
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
    public void checkInDorm(@RequestParam String dormitoryId, @RequestParam String username){
        if (dormitoryId==null||username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        else{
            user user = userService.findByUsername(username);
            if (user.getLeaderId().getId() != user.getId()){
                throw new MyException(Code.UNAUTHORISED_NOT_LEADER);
            }
            if (dormitoryService.checkRoomAvailable(dormitoryId)){
                dormitory dormitory = dormitoryService.findById(dormitoryId);
                userService.bookRoom(user,dormitory);
            }
            else {
                throw new MyException(Code.Room_Occupied);
            }
        }
    }
}
