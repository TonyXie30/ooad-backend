package com.dormitory.backend.web;

import com.dormitory.backend.api.UserRepository;
import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TeamController {
    @Autowired
    private UserService userService;
    @CrossOrigin("http://localhost:8080")
    @PostMapping(value = "api/teamUp")
    @ResponseBody
    public user teamUp(@RequestParam String memberName, @RequestParam String leaderName){
        try{
            user member = userService.findByUsername(memberName);
            user leader = userService.findByUsername(leaderName);
            if(leader==null||member==null){
                throw new MyException(Code.USER_NOT_EXIST);
            }
            if(leader.getId()==member.getId()){
                // 已存在的队长。
                // 不视为异常，但也不进行数据库交互。
                System.out.println("existed leader");
                return member;
            }
            userService.teamUp(member,leader);
            return member;
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        throw new MyException(Code.METHOD_FAILED);
    }

    @CrossOrigin("http://localhost:8080")
    @PostMapping(value = "api/leaveTeam")
    @ResponseBody
    public user leaveTeam(String username){
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        user user = userService.findByUsername(username);
        if(user==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        userService.teamUp(user,user);
        return user;
    }
}
