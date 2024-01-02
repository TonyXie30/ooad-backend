package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.User;
import com.dormitory.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
@RestController
public class LoginController {

    @Autowired
    private UserService userService;


    @PostMapping(value = "api/login")
    @Transactional //avoid no session exception
    @ResponseBody
    public User login(@RequestBody User requestUser, HttpServletRequest request) {
        // 从数据库中查找用户信息
        HttpSession session = request.getSession();
        User user = userService.findByUsername(requestUser.getUsername());
        // 如果用户不存在或密码不匹配，返回登录失败
        if (user == null || !requestUser.getPassword().equals(user.getPassword())) {
            throw new MyException(Code.LOGIN_FAILED);
        } else {
            session.setAttribute("username",user.getUsername());
            Hibernate.initialize(user.getBookmark());
//            Hibernate.initialize(User.getBedtime());
//            Hibernate.initialize(User.getWakeupTime());
            return user;
        }
    }


    @PostMapping(value = "api/register")
    @ResponseBody
    public User register(@RequestBody @NotNull User requestUser){
        String username = requestUser.getUsername();
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        User user = userService.findByUsernameUnCheck(username);
        if(user != null){
            throw new MyException(Code.REG_EXISTED_USER);
        }

        String pw = requestUser.getPassword();
        if(pw == null||!pw.matches("\\S+")){
            throw new MyException(Code.REG_BAD_PASSWORD_FORMAT);
        }
        requestUser.setAdmin(false);
        requestUser.setGender(requestUser.getGender());
        requestUser.setDegree(requestUser.getDegree());
        user = userService.register(requestUser);
        userService.teamUp(user,user);
        return user;
    }


}

