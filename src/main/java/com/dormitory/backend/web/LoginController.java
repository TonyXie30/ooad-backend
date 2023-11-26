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
import org.springframework.web.util.HtmlUtils;

import java.util.Objects;
import jakarta.servlet.http.HttpSession;
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @CrossOrigin("http://localhost:8080")
    @PostMapping(value = "api/login")
    @Transactional
    @ResponseBody
    public user login(@RequestBody user requestUser, HttpServletRequest request) {

        String username = requestUser.getUsername();
        // 从数据库中查找用户信息
        HttpSession session = request.getSession();
        user user = userService.findByUsername(username);
        Hibernate.initialize(user.getBookmark());
        Hibernate.initialize(user.getBedtime());
        Hibernate.initialize(user.getWakeupTime());
        // 如果用户不存在或密码不匹配，返回登录失败
        if (user == null || !Objects.equals(requestUser.getPassword(), user.getPassword())) {
            throw new MyException(Code.LOGIN_FAILED);
        } else {
            session.setAttribute("username",user.getUsername());
            return user;
        }
    }

    @CrossOrigin("http://localhost:8080")
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
            return userService.register(requestUser);
        }

    }
}

