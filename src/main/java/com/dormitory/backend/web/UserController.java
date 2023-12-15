package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.*;
import com.dormitory.backend.service.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @CrossOrigin
    @PostMapping(value = "api/checkUserIsCheckedIn")
    @ResponseBody
    public boolean checkUserIsCheckedIn(@RequestBody String username){
        user userInDB = this.checkUser(username);
        return userInDB.getBookedDormitory()!=null;
    }

    @CrossOrigin
    @PostMapping(value = "api/getUsers")
    @ResponseBody
    public Page<user> getUsers(@RequestParam(required = false) Integer page,
                               @RequestParam(required = false) Integer limit,
                               @RequestParam(required = false) String sort,
                               @RequestParam(required = false,value = "sortBy[]") String[] sortBy){

        if(sortBy==null) {
            return userService.getUsers(page, limit, sort);
        } else{
            List<String> sortByList = new ArrayList<>(List.of(sortBy));
            Field[] fields = user.class.getDeclaredFields();
            List<String> attr = new ArrayList<>();
            for (Field f:fields) {
                f.setAccessible(true);
                for (int i = 0; i < sortByList.size(); i++) {
                    String by = sortByList.get(i);
                    if(by.equals(f.getName())){
                        attr.add(f.getName());
                        sortByList.remove(i);
                        System.out.println("match: "+f.getName());
                        break;
                    }
                }
            }
            return userService.getUsers(page, limit, sort,attr.toArray(new String[0]));
        }
    }

    @CrossOrigin
    @PostMapping(value = "api/getUser")
    @ResponseBody
    public user getUser(@RequestParam String username){
        return this.checkUser(username);
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
    public void setComment(@RequestParam String username,@RequestParam String dormitoryId,@RequestParam String content,@RequestParam(required = false) Integer parentId){
        if (username==null||dormitoryId==null)
            throw new MyException(Code.MISSING_FIELD);
        userService.setComment(username,dormitoryId,content,parentId);
    }
    @PostMapping(value = "api/deleteComment")
    @Transactional
    @ResponseBody
    public void deleteComment(@RequestParam int comment_id){

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
                              @RequestParam @Schema(description = "hh:mm:ss (请先在timeRange表插入数据)") Time time,
                              @RequestParam @Schema(description = "标识设置起床或入睡，0表示起床，1表示睡觉") Integer type){
//        type:0 - 起床，1 - 睡觉
//        start 的 01 分别表示时间段的 起止
        if(username==null||time==null||type==null){
            throw new MyException(Code.MISSING_FIELD);
        }

        user userInDB = userService.findByUsername(username);
        if(userInDB==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }

        timeRange time_ = userService.findTimeSlot(time);
        if(time_==null){
            throw new MyException(Code.TIME_NOT_EXIST);
        }

        if(type==0){
            userService.setUpTime(userInDB,time_);
        } else if (type==1) {
            userService.setBedTime(userInDB,time_);
        }
    }

    @PostMapping(value = "api/communicate")
    @ResponseBody
    public void communicate(@RequestParam String receiver_name,@RequestParam String sender_name,
                            @RequestParam String content){
        user sender = userService.findByUsername(sender_name);
        user receiver = userService.findByUsername(receiver_name);
        userService.communicate(sender,receiver,content);
    }

    @PostMapping(value = "api/checkMailbox")
    @ResponseBody
    public List<Notification> checkMailbox(@RequestParam String username){
        user user = userService.findByUsername(username);
        return userService.checkMailbox(user);
    }

    public user checkUser(String username){
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        user userInDB = userService.findByUsername(username);
        if(userInDB==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        return userInDB;
    }
}
