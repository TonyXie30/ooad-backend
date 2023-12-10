package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.comment;
import com.dormitory.backend.pojo.dormitory;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @CrossOrigin
    @PostMapping(value = "api/checkUserIsCheckedIn")
    @ResponseBody
    public boolean checkUserIsCheckedIn(@RequestBody String username){
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        user userInDB = userService.findByUsername(username);
        if(userInDB==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        return userInDB.getBookedDormitory()!=null;
    }

    @CrossOrigin
    @PostMapping(value = "api/getUsers")
    @ResponseBody
    public Page<user> getUsers(@RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer limit,
                                  @RequestParam(required = false) String sort){
        return userService.getUsers(page, limit, sort);
    }

    @PostMapping(value = "api/getComment")
    @ResponseBody
    public List<comment> getComment(@RequestParam Integer dormitoryId, @RequestParam Integer parentId){
        if (dormitoryId==null) //在数据库中0就是null，输入0就是无parent
            throw new MyException(Code.MISSING_FIELD);
        return userService.getComment(dormitoryId,parentId);
    }

    @PostMapping(value = "api/setComment")
    @Transactional
    @ResponseBody
    public void setComment(@RequestBody comment object, user user, dormitory dormitory, String content, Integer parentId){
        if (dormitory==null||user==null||content==null)
            throw new MyException(Code.MISSING_FIELD);
        userService.setComment(object,user,dormitory,content,parentId);
    }


    @PostMapping(value = "api/getBookMark")
    @Transactional
    @ResponseBody
    public List<dormitory> getBookMark(@RequestParam String username){
        if (username==null||userService.findByUsername(username)==null)
            throw new MyException(Code.MISSING_FIELD);
        else {
            return userService.getBookMark(username);
        }
    }


    @PostMapping(value = "api/setBookMark")
    @Transactional
    @ResponseBody
    public void setBookMark(@RequestParam String dormitoryId,@RequestParam String username){
        if (username==null||dormitoryId==null)
            throw new MyException(Code.MISSING_FIELD);
        userService.setBookMark(dormitoryId,username);
    }

    @PostMapping(value = "api/setFavourTime")
    @ResponseBody
    public void setFavourTime(@RequestParam String username,
                              @RequestParam Time time,
                              @RequestParam @Schema(description = "标识设置起床或入睡，0表示起床，1表示睡觉") Integer type,
                              @RequestParam @Schema(description = "用0/1标识设置时段的起/止") Integer start){
//        type:0 - 起床，1 - 睡觉
//        start 的 01 分别表示时间段的 起止
        if(username==null||time==null||type==null||start==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        user userInDB = userService.findByUsername(username);
        if(userInDB==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        if(type==0){
            if(start==0){
                userService.setUpTimeStart(userInDB,time);
            } else if (start==1) {
                userService.setUpTimeEnd(userInDB,time);
            }
        } else if (type==1) {
            if(start==0){
                userService.setBedTimeStart(userInDB,time);
            } else if (start==1) {
                userService.setBedTimeEnd(userInDB,time);
            }
        }
    }
}
