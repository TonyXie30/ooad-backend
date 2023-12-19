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
        if(dormitory==null){
            throw new MyException(Code.MISSING_FIELD);
        }
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
    @PostMapping(value = "api/admin/getSelectionTime")
    @ResponseBody
    public SelectionTimeConfig getSelectionTime(@RequestParam String gender,@RequestParam String degree){
        SelectionTimeConfig configInDB = dormitoryService.getSelectionTime(
                userService.getGender(gender), userService.getDegree(degree));
        if (configInDB==null){
            throw new MyException(Code.GENERAL_NOT_EXIST);
        }
        return configInDB;
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
