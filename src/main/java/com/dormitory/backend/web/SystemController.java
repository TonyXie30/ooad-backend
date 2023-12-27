package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.ExchangeApplicationCache;
import com.dormitory.backend.pojo.UserProjection;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Documented;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SystemController {

    /**
    Cache:
    尝试在内存中完成操作，减少对数据库的io操作。
    */
    ConcurrentHashMap<String, Set<String>> exchangeApplicationCache = new ConcurrentHashMap<>();
    ExchangeApplicationCache cache = new ExchangeApplicationCache();
    @Autowired
    UserService userService;

    @PreDestroy
    private void saveExchangeApplicationCache(){
        cache.save(cache);
        userService.saveExchangeApplicationCache(exchangeApplicationCache);
        exchangeApplicationCache.clear();
        exchangeApplicationCache = null;
    }

    @PostMapping("api/exchangeApply")
    @ResponseBody
    public String exchangeApply(@RequestParam String username,@RequestParam String to){

        user touser = checkApplicationCache(to);
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
            Set<String> applicationList = cache.get(to);
            applicationList.add(username);
            exchangeApplicationCache.put(to,applicationList);
            return "warning: one student has not checked in yet";
        } else{
//            Both checked in
            if (user.getBookedDormitory().getId()==touser.getBookedDormitory().getId())
                throw new MyException(Code.EXCHANGE_SAME_DORMITORY);
            Set<String> applicationList = cache.get(to);
            applicationList.add(username);
            exchangeApplicationCache.put(to,applicationList);
            return "success";
        }
    }

    @PostMapping("api/exchangeAccept")
    @ResponseBody
    public void exchangeAccept(@RequestParam String username,@RequestParam String from){
        user user = checkApplicationCache(username);
        user fromUser = userService.findByUsername(from);

        Set<String> applicationList = cache.get(username);
        if (!applicationList.contains(from)){
            throw new MyException(Code.EXCHANGE_APPLICATION_NOT_EXIST);
        }
        userService.exchangeRoom(user,fromUser);
        applicationList.remove(from);
    }
    @PostMapping("api/exchangeReject")
    @ResponseBody
    public void exchangeReject(@RequestParam String username,@RequestParam String from){
        checkApplicationCache(username);

        Set<String> applicationList = cache.get(username);
        if (!applicationList.contains(from)){
            throw new MyException(Code.EXCHANGE_APPLICATION_NOT_EXIST);
        }
        applicationList.remove(from);
    }

    @PostMapping("api/getExchangeApplications")
    @ResponseBody
    public Set<String> getExchangeApplications(@RequestParam String username){
        if (userService.findByUsername(username)==null) throw new MyException(Code.USER_NOT_EXIST);
        return cache.get(username);
    }

    private user checkApplicationCache(String username){
        user user = userService.findByUsername(username);
        if (user==null) throw new MyException(Code.USER_NOT_EXIST);
        if(exchangeApplicationCache.)
            exchangeApplicationCache.putIfAbsent(username,user.getExchangeApplicationNameList());
        return user;
    }

    @PostMapping("api/recommend")
    @ResponseBody
    public List<UserProjection> recommendFriend(@RequestParam String username){

        return userService.recommendFriend(username);
    }
}
