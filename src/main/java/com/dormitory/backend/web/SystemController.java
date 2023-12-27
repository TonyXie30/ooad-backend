package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.UserProjection;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class SystemController {

    /**
    Cache:
    尝试在内存中完成操作，减少对数据库的io操作。
    */
    @Autowired
    UserService userService;

    @PostMapping("api/exchangeApply")
    @ResponseBody
    public String exchangeApply(@RequestParam String username,@RequestParam String to){

        user touser = userService.findByUsername(to);
        user user = userService.findByUsername(username);
//        user touser = userService.findByUsername(to);
        if (user==null) throw new MyException(Code.USER_NOT_EXIST);
        if (touser==null) throw new MyException(Code.USER_NOT_EXIST);
        if (!user.getGender().equals(touser.getGender())||
        !user.getDegree().equals(touser.getDegree())){
            throw new MyException(Code.EXCHANGE_TYPE_NOT_MATCH);
        }
        if (user.getBookedDormitory()==null && touser.getBookedDormitory()==null){
            throw new MyException(Code.EXCHANGE_NULL_DORMITORY);
        }

        if (user.getBookedDormitory()==null||touser.getBookedDormitory()==null) {
            Set<user> applicationList = userService.getExchangeApplicationList(to);
            applicationList.add(user);
            touser.setExchangeApplication(applicationList);
            userService.updateUser(touser);
            return "warning: one student has not checked in yet";
        } else{
//            Both checked in
            if (user.getBookedDormitory().getId()==touser.getBookedDormitory().getId())
                throw new MyException(Code.EXCHANGE_SAME_DORMITORY);
            Set<user> applicationList = userService.getExchangeApplicationList(to);
            applicationList.add(user);
            touser.setExchangeApplication(applicationList);
            userService.updateUser(touser);
            return "success";
        }
    }

    @PostMapping("api/exchangeAccept")
    @ResponseBody
    public void exchangeAccept(@RequestParam String username,@RequestParam String from){
        exchangeOpe(username, from, 0);
    }
    @PostMapping("api/exchangeReject")
    @ResponseBody
    public void exchangeReject(@RequestParam String username,@RequestParam String from){
        exchangeOpe(username, from, 1);
    }
    private void exchangeOpe(String username, String from, int type) {
        user user_ = userService.findByUsername(username);
        user fromUser = userService.findByUsername(from);
        Set<user> applicationList = userService.getExchangeApplicationList(username);
        if (!applicationList.contains(fromUser)){
            throw new MyException(Code.EXCHANGE_APPLICATION_NOT_EXIST);
        }
        applicationList.remove(fromUser);
        user_.setExchangeApplication(applicationList);
        switch (type){
            case 0 -> userService.exchangeRoom(user_,fromUser);
            case 1 -> userService.updateUser(user_);
        }
    }

    @PostMapping("api/getExchangeApplications")
    @ResponseBody
    public Set<user> getExchangeApplications(@RequestParam String username){
        if (userService.findByUsername(username)==null) throw new MyException(Code.USER_NOT_EXIST);
        return userService.getExchangeApplicationList(username);
    }

    private user checkApplicationCache(String username){
        user user = userService.findByUsername(username);
        if (user==null) throw new MyException(Code.USER_NOT_EXIST);
//        if(exchangeApplicationCache.)
//            exchangeApplicationCache.putIfAbsent(username,user.getExchangeApplicationNameList());
        return user;
    }

    @PostMapping("api/recommend")
    @ResponseBody
    public List<UserProjection> recommendFriend(@RequestParam String username){

        return userService.recommendFriend(username);
    }
}
