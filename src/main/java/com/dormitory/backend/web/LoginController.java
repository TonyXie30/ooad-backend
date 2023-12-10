package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.dormitory.backend.pojo.user;
import com.dormitory.backend.pojo.comment;
import com.dormitory.backend.pojo.dormitory;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.servlet.http.HttpSession;
@RestController
public class LoginController {

    @Autowired
    private UserService userService;


    @PostMapping(value = "api/login")
    @Transactional //avoid no session exception
    @ResponseBody
    public user login(@RequestBody user requestUser, HttpServletRequest request) {
        // 从数据库中查找用户信息
        HttpSession session = request.getSession();
        user user = userService.findByUsername(requestUser.getUsername());
        System.out.println(session);
        // 如果用户不存在或密码不匹配，返回登录失败
        if (user == null || !requestUser.getPassword().equals(user.getPassword())) {
            throw new MyException(Code.LOGIN_FAILED);
        } else {
            session.setAttribute("username",user.getUsername());
            Hibernate.initialize(user.getBookmark());
            Hibernate.initialize(user.getBedtime());
            Hibernate.initialize(user.getWakeupTime());
            return user;
        }
    }


    @PostMapping(value = "api/register")
    @ResponseBody
    public user register(@RequestBody @NotNull user requestUser){
        String username = requestUser.getUsername();
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        user user = userService.findByUsername(username);

        if(user!=null){
            throw new MyException(Code.REG_EXISTED_USER);
        } else {
            String pw = requestUser.getPassword();
            if(pw == null||!pw.matches("\\S+")){
                throw new MyException(Code.REG_BAD_PASSWORD_FORMAT);
            }
            requestUser.setAdmin(false);
            user = userService.register(requestUser);
            userService.teamUp(user,user);
            return user;
        }
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
    public void setComment(@RequestBody comment object,@RequestParam String username,@RequestParam String dormitoryId,@RequestParam String content,@RequestParam Integer parentId){
        if (username==null||dormitoryId==null)
            throw new MyException(Code.MISSING_FIELD);
        userService.setComment(object,username,dormitoryId,content,parentId);
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
}

