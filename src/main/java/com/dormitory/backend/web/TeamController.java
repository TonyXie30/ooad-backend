package com.dormitory.backend.web;

import com.dormitory.backend.api.UserRepository;
import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @CrossOrigin("http://localhost:8080")
    @PostMapping(value = "api/teamUp")
    @ResponseBody
    public user teamUp(user user,int leaderId){
        if(user==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        try{
            user leader = userRepository.findById(leaderId);
            if(leader==null){
                throw new MyException(Code.USER_NOT_EXIST);
            }
            if(leaderId==user.getId()){
                // 已存在的队长。
                // 不视为异常，但也不进行数据库交互。
                System.out.println("existed leader");
                return user;
            }
            userService.teamUp(user,leaderId);
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        return user;
    }
    @CrossOrigin("http://localhost:8080")
    @PostMapping(value = "api/leaveTeam")
    @ResponseBody
    public user leaveTeam(user user){
        if(user==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        userService.teamUp(user,user.getId());
        return user;
    }


}
