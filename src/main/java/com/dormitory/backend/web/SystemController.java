package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.UserProjection;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SystemController {

     /**
    Cache:
    尝试在内存中完成操作，减少对数据库的io操作。
     */
    ConcurrentHashMap<String, List<String>> exchangeApplicationCache = new ConcurrentHashMap<>();
    @Autowired
    UserService userService;

    @PostMapping("api/exchangeApply")
    @ResponseBody
    public String exchangeApply(@RequestParam String username,@RequestParam String to){
        user user = userService.findByUsername(username);
        user touser = userService.findByUsername(username);
        if (user==null) throw new MyException(Code.USER_NOT_EXIST);
        if (touser==null) throw new MyException(Code.USER_NOT_EXIST);
        if (user.getBookedDormitory()==null && touser.getBookedDormitory()==null){
            throw new MyException(Code.EXCHANGE_NULL_DORMITORY);
        }
        if (user.getBookedDormitory()==null||touser.getBookedDormitory()==null) {
            List<String> applicationList = exchangeApplicationCache.getOrDefault(to, new ArrayList<>());
            applicationList.add(username);
            exchangeApplicationCache.put(to,applicationList);
            return "warning: one student has not checked in yet";
        } else{
//            Both checked in
            if (user.getBookedDormitory().getId()==touser.getBookedDormitory().getId())
                throw new MyException(Code.EXCHANGE_SAME_DORMITORY);
            List<String> applicationList = exchangeApplicationCache.getOrDefault(to, new ArrayList<>());
            applicationList.add(username);
            exchangeApplicationCache.put(to,applicationList);
            return "success";
        }
    }

    @PostMapping("api/exchangeAccept")
    @ResponseBody
    public void exchangeAccept(@RequestParam String username,@RequestParam String from){
        user user = userService.findByUsername(username);
        user fromUser = userService.findByUsername(from);
        if (user==null) throw new MyException(Code.USER_NOT_EXIST);
        if (fromUser==null) throw new MyException(Code.USER_NOT_EXIST);

        List<String> applicationList = exchangeApplicationCache.getOrDefault(username, new ArrayList<>());
        if (!applicationList.contains(from)){
            throw new MyException(Code.EXCHANGE_APPLICATION_NOT_EXIST);
        }
        userService.exchangeRoom(user,fromUser);
        applicationList.remove(from);
    }
    @PostMapping("api/exchangeReject")
    @ResponseBody
    public void exchangeReject(@RequestParam String username,@RequestParam String from){
        if (userService.findByUsername(username)==null) throw new MyException(Code.USER_NOT_EXIST);
        if (userService.findByUsername(from)==null) throw new MyException(Code.USER_NOT_EXIST);

        List<String> applicationList = exchangeApplicationCache.getOrDefault(username, new ArrayList<>());
        if (!applicationList.contains(from)){
            throw new MyException(Code.EXCHANGE_APPLICATION_NOT_EXIST);
        }
        applicationList.remove(from);
    }

    @PostMapping("api/getExchangeApplications")
    @ResponseBody
    public List<String> getExchangeApplications(@RequestParam String username){
        if (userService.findByUsername(username)==null) throw new MyException(Code.USER_NOT_EXIST);
        return exchangeApplicationCache.getOrDefault(username, new ArrayList<>());
    }

    @PostMapping("api/recommend")
    @ResponseBody
    public List<UserProjection> recommendFriend(@RequestParam String username){

        return userService.recommendFriend(username);
    }
}
