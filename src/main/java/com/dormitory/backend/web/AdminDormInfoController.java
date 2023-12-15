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
    public void setSelectionTime(@RequestBody SelectionTimeConfig config){
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
        dormitoryService.setSelectionTime(config);
    }
}
