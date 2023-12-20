package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.Degree;
import com.dormitory.backend.pojo.Gender;
import com.dormitory.backend.pojo.SelectionTimeConfig;
import com.dormitory.backend.pojo.dormitory;
import com.dormitory.backend.service.DormitoryService;
import com.dormitory.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class AdminDormInfoController {
    //尚未完成用户权限控制以方便调试
    @Autowired
    private DormitoryService dormitoryService;
    @Autowired
    private UserService userService;

    @CrossOrigin
    @PostMapping(value = "api/admin/addDormitory")
    @ResponseBody
    public dormitory addDormitory(@RequestBody dormitory dormitory){
        if(dormitory==null||dormitory.getGender().getGender()==null||dormitory.getDegree().getDegree()==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        dormitory.setGender(userService.getGender(dormitory.getGender().getGender()));
        dormitory.setDegree(userService.getDegree(dormitory.getDegree().getDegree()));
        return dormitoryService.addDormitory(dormitory);
    }
    @CrossOrigin
    @PostMapping(value = "api/admin/modifyDormitory")
    @ResponseBody
    public dormitory modifyDormitory(@RequestBody dormitory dormitory){
        if(dormitory==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        dormitory dor_DB = dormitoryService.findById(Integer.toString(dormitory.getId()));
        if(dor_DB==null){
            throw new MyException(Code.DORMITORY_NOT_EXIST);
        }
        if (dormitory.getGender()!=null && !Objects.equals(dormitory.getGender().getGender(), dor_DB.getGender().getGender()))
            dor_DB.setGender(userService.getGender(dormitory.getGender().getGender()));
        if (dormitory.getDegree()!=null && !Objects.equals(dormitory.getDegree().getDegree(), dor_DB.getDegree().getDegree()))
            dor_DB.setDegree(userService.getDegree(dormitory.getDegree().getDegree()));
        if (dormitory.getFloor()!=0 && dormitory.getFloor()!=dor_DB.getFloor())
            dor_DB.setFloor(dormitory.getFloor());
        if (dormitory.getBuildingName()!=null && !Objects.equals(dormitory.getBuildingName(), dor_DB.getBuildingName()))
            dor_DB.setBuildingName(dormitory.getBuildingName());
        if (dormitory.getHouseNum()!=null && !Objects.equals(dormitory.getHouseNum(), dor_DB.getHouseNum()))
            dor_DB.setHouseNum(dormitory.getHouseNum());
        if  (dormitory.getLocation()!=null && !Objects.equals(dormitory.getLocation(), dor_DB.getLocation()))
            dor_DB.setLocation(dormitory.getLocation());
        if (dormitory.getBed()!=0 && dormitory.getBed()!=dor_DB.getBed())
            dor_DB.setBed(dormitory.getBed());
        return dormitoryService.addDormitory(dormitory);
    }

    @CrossOrigin
    @PostMapping(value = "api/admin/removeDormitory")
    @ResponseBody
    public dormitory removeDormitory(@RequestBody dormitory dormitory){
        if(dormitory==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        if(dormitoryService.checkRoomExisted(dormitory)==null){
            throw new MyException(Code.DORMITORY_NOT_EXIST);
        }
        dormitoryService.removeDormitory(dormitory);
        return dormitory;
    }

    @CrossOrigin
    @PostMapping(value = "api/admin/setSelectionTime")
    @ResponseBody
    public String setSelectionTime(@RequestBody SelectionTimeConfig config){
        SelectionTimeConfig config_ = buildConfig(config);
        SelectionTimeConfig configInDB = dormitoryService.getSelectionTime(config_.getGender(),config_.getDegree());
        if (configInDB!=null){
            configInDB.setStartTime(config.getStartTime());
            configInDB.setEndTime(config.getEndTime());
            dormitoryService.setSelectionTime(configInDB);
//            注意这不是异常。
            return "modify an existed time slot";
        } else {
            dormitoryService.setSelectionTime(config_);
            return "create a new time slot";
        }
    }

    @CrossOrigin
    @PostMapping(value = "api/admin/deleteSelectionTime")
    @ResponseBody
    public void deleteSelectionTime(@RequestBody SelectionTimeConfig config){
        SelectionTimeConfig config_ = buildConfig(config);
        SelectionTimeConfig configInDB = dormitoryService.getSelectionTime(config_.getGender(),config_.getDegree());
        if (configInDB==null){
            throw new MyException(Code.GENERAL_NOT_EXIST);
        }
        dormitoryService.deleteSelectionTime(configInDB);
    }

//    模板方法
    public SelectionTimeConfig buildConfig(SelectionTimeConfig config){
        if(config.getGender()==null||config.getDegree()==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        Gender gender = userService.getGender(config
                .getGender() //获取Gender对象
                .getGender() /*获取Gender名*/);
        Degree degree = userService.getDegree(config
                .getDegree() //获取Gender对象
                .getDegree() /*获取Gender名*/);
        if(gender==null||degree==null){
            throw new MyException(Code.GENERAL_NOT_EXIST);
        }
        config.setDegree(degree);
        config.setGender(gender);
        return config;
    }
}
