package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @CrossOrigin
    @PostMapping(value = "api/checkUserIsCheckedIn")
    @ResponseBody
    public boolean checkUserIsCheckedIn(@RequestBody user user){
        if(user==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        user userInDB = userService.findByUsername(user.getUsername());
        if(userInDB==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        return userInDB.getBookedDormitory()!=null;
    }
}