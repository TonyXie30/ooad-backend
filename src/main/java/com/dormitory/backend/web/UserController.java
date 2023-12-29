package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.*;
import com.dormitory.backend.service.CommentService;
import com.dormitory.backend.service.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;

    @CrossOrigin
    @PostMapping(value = "api/checkUserIsCheckedIn")
    @ResponseBody
    public boolean checkUserIsCheckedIn(@RequestParam String username) {
        user userInDB = userService.findByUsername(username);
        return userInDB.getBookedDormitory() != null;
    }

    @CrossOrigin
    @PostMapping(value = "api/getUsers")
    @ResponseBody
    public Page<user> getUsers(@RequestParam(required = false) Integer page,
                               @RequestParam(required = false) Integer limit,
                               @RequestParam(required = false) String sort,
                               @RequestParam(required = false) String username,
                               @RequestParam(required = false, value = "sortBy[]") String[] sortBy) {

        if (sortBy==null){
            sortBy = new String[]{"id"};
        }
        List<String> sortByList = new ArrayList<>(List.of(sortBy));
        Field[] fields = user.class.getDeclaredFields();
        List<String> attr = new ArrayList<>();
        for (Field f : fields) {
            f.setAccessible(true);
            for (int i = 0; i < sortByList.size(); i++) {
                String by = sortByList.get(i);
                if (by.equals(f.getName())) {
                    attr.add(f.getName());
                    sortByList.remove(i);
                    System.out.println("match: " + f.getName());
                    break;
                }
            }
        }

        if (username!=null){
            user hostUser = userService.findByUsername(username);
            if(hostUser==null) throw new MyException(Code.USER_NOT_EXIST);
            return userService.getUsers(page, limit, sort, hostUser, attr.toArray(new String[0]));
        }else {
            return userService.getUsers(page, limit, sort, attr.toArray(new String[0]));
        }
    }

    @CrossOrigin
    @PostMapping(value = "api/getUser")
    @ResponseBody
    public user getUser(@RequestParam String username) {
        return userService.findByUsername(username);
    }

    @PostMapping(value = "api/getComment")
    @ResponseBody
    public List<comment> getComment(@RequestParam Integer dormitoryId, @RequestParam Integer parentId) {
        if (dormitoryId == null) //在数据库中0就是null，输入0就是无parent
            throw new MyException(Code.MISSING_FIELD);
        return userService.getComment(dormitoryId, parentId);
    }

    @PostMapping(value = "api/setComment", produces = "application/json")
    @Transactional
    @ResponseBody
    public Map<String, Object> setComment(@RequestParam String username, @RequestParam String dormitoryId, @RequestParam String content, @RequestParam(required = false) Integer parentId) {
        if (username == null || dormitoryId == null)
            throw new MyException(Code.MISSING_FIELD);
        comment comment = userService.setComment(username, dormitoryId, content, parentId);
        Map<String, Object> map = new HashMap<>();
        map.put("comment_id", comment.getId());
        map.put("parent_id", comment.getParent().getId());
        map.put("create_time", comment.getCreate_time());
        return map;
    }

    @PostMapping(value = "api/updateUser")
    @Transactional
    @ResponseBody
    public user updateUser(@RequestBody user user) {
        if(user.getUsername()==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        user userInDB = userService.findByUsername(user.getUsername());
        if (userInDB == null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        Field[] fieldsDB = userInDB.getClass().getDeclaredFields();
        Field[] fields =  user.getClass().getDeclaredFields();
        try {
            for (Field fieldDB:fieldsDB) {
                for (Field field : fields) {
                    fieldDB.setAccessible(true);
                    field.setAccessible(true);
                    String attributeName=field.getName();
                    if(attributeName.equals("bookedDormitory") ||
                            attributeName.equals("bookmark") ||
                            attributeName.equals("id")){
                        continue;
                    }
                    if (field.getName().equals(fieldDB.getName()) &&
                            field.get(user) != null &&
                            !field.get(user).equals(fieldDB.get(userInDB))){


                        String methodName=attributeName.substring(0,1).toUpperCase()+attributeName.substring(1);

                        Method set = userInDB
                                .getClass()
                                .getMethod("set" + methodName,fieldDB.getType());
                        set.invoke(userInDB, field.get(user));
                    }
                }
            }
        }catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return userService.updateUser(userInDB);
    }



    @PostMapping(value = "api/getBookMark")
    @Transactional
    @ResponseBody
    public List<dormitory> getBookMark(@RequestParam String username) {
        user author = userService.findByUsername(username);
        return userService.getBookMark(author);
    }


    @PostMapping(value = "api/setBookMark")
    @Transactional
    @ResponseBody
    public void setBookMark(@RequestParam String dormitoryId, @RequestParam String username) {
        if (username == null || dormitoryId == null)
            throw new MyException(Code.MISSING_FIELD);
        userService.setBookMark(dormitoryId, username);
    }

    @PostMapping(value = "api/setFavourTime")
    @ResponseBody
    public void setFavourTime(@RequestParam String username,
                              @RequestParam @Schema(description = "hh:mm:ss (请先在timeRange表插入数据)") Time time,
                              @RequestParam @Schema(description = "标识设置起床或入睡，0表示起床，1表示睡觉") Integer type) {
//        type:0 - 起床，1 - 睡觉
//        start 的 01 分别表示时间段的 起止
        if (username == null || time == null || type == null) {
            throw new MyException(Code.MISSING_FIELD);
        }

        user userInDB = userService.findByUsername(username);

        timeRange time_ = userService.findTimeSlot(time);
        if (time_ == null) {
            throw new MyException(Code.TIME_NOT_EXIST);
        }

        if (type == 0) {
            userService.setUpTime(userInDB, time_);
        } else if (type == 1) {
            userService.setBedTime(userInDB, time_);
        }
    }

    @PostMapping(value = "api/communicate")
    @ResponseBody
    public void communicate(@RequestParam String receiver_name, @RequestParam String sender_name,
                            @RequestParam String content) {
        user sender = userService.findByUsername(sender_name);
        user receiver = userService.findByUsername(receiver_name);
        userService.communicate(sender, receiver, content);
    }

    @PostMapping(value = "api/checkMailbox")
    @ResponseBody
    public List<Notification> checkMailbox(@RequestParam String username) {
        user user = userService.findByUsername(username);
        return userService.checkMailbox(user);
    }

    @PostMapping(value = "api/getRoomCheckInedUsers")
    @ResponseBody
    public List<user> getRoomCheckInedUsers(@RequestParam Integer dormitoryid) {
        return userService.getRoomCheckInedUsers(dormitoryid);
    }
}
