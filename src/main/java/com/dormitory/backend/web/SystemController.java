package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.User;
import com.dormitory.backend.pojo.UserProjection;
import com.dormitory.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SystemController {

    /**
    Cache:
    尝试在内存中完成操作，减少对数据库的io操作。
    */
    @Autowired
    UserService userService;


    @PostMapping("api/exchangeRoom")
    @ResponseBody
    public void exchangeRoom(@RequestParam String username,@RequestParam String to){
        User user = userService.findByUsername(username);
        User touser = userService.findByUsername(to);
        if (user.getBookedDormitory() == null && touser.getBookedDormitory() == null) {
            throw new MyException(Code.EXCHANGE_NULL_DORMITORY);
        }
        if (user.getBookedDormitory().getId() == touser.getBookedDormitory().getId()) {
            throw new MyException(Code.EXCHANGE_SAME_DORMITORY);
        }
        userService.exchangeRoom(user, touser);
    }

    /*
   //    public String exchangeApply(@RequestParam String username,@RequestParam String to){
   //
   //        User touser = userService.findByUsername(to);
   //        User User = userService.findByUsername(username);
   ////        User touser = userService.findByUsername(to);
   //        if (User==null) throw new MyException(Code.USER_NOT_EXIST);
   //        if (touser==null) throw new MyException(Code.USER_NOT_EXIST);
   //        if (!User.getGender().equals(touser.getGender())||
   //        !User.getDegree().equals(touser.getDegree())){
   //            throw new MyException(Code.EXCHANGE_TYPE_NOT_MATCH);
   //        }
   //        if (User.getBookedDormitory()==null && touser.getBookedDormitory()==null){
   //            throw new MyException(Code.EXCHANGE_NULL_DORMITORY);
   //        }
   //
   //        if (User.getBookedDormitory()==null||touser.getBookedDormitory()==null) {
   //            Set<User> applicationList = userService.getExchangeApplicationList(to);
   //            applicationList.add(User);
   //            touser.setExchangeApplication(applicationList);
   //            userService.updateUser(touser);
   //            return "warning: one student has not checked in yet";
   //        } else{
   ////            Both checked in
   //            if (User.getBookedDormitory().getId()==touser.getBookedDormitory().getId())
   //                throw new MyException(Code.EXCHANGE_SAME_DORMITORY);
   //            Set<User> applicationList = userService.getExchangeApplicationList(to);
   //            applicationList.add(User);
   //            touser.setExchangeApplication(applicationList);
   //            userService.updateUser(touser);
   //            return "success";
   //        }
   //    }
        */
    @PostMapping("api/exchangeApply")
    @ResponseBody

    public String exchangeApply(@RequestParam String username,@RequestParam String to) {
        User user = userService.findByUsername(username);
        User touser = userService.findByUsername(to);
        if (user.getBookedDormitory() == null && touser.getBookedDormitory() == null) {
            throw new MyException(Code.EXCHANGE_NULL_DORMITORY);
        }
        if (user.getBookedDormitory().getId() == touser.getBookedDormitory().getId()) {
            throw new MyException(Code.EXCHANGE_SAME_DORMITORY);
        }
        userService.exchangeApply(user, touser);
        return "success";
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
        User user_ = userService.findByUsername(username);
        User fromUser = userService.findByUsername(from);
        switch (type){
            case 0 -> userService.exchangeAcceptNotification(user_,fromUser);
            case 1 -> userService.exchangeRejectNotification(user_,fromUser);
        }
    }

//    @PostMapping("api/getExchangeApplications")
//    @ResponseBody
//    public Set<User> getExchangeApplications(@RequestParam String username){
//        if (userService.findByUsername(username)==null) throw new MyException(Code.USER_NOT_EXIST);
//        return userService.getExchangeApplicationList(username);
//    }

    private User checkApplicationCache(String username){
        User user = userService.findByUsername(username);
        if (user==null) throw new MyException(Code.USER_NOT_EXIST);
//        if(exchangeApplicationCache.)
//            exchangeApplicationCache.putIfAbsent(username,User.getExchangeApplicationNameList());
        return user;
    }

    @PostMapping("api/recommend")
    @ResponseBody
    public List<UserProjection> recommendFriend(@RequestParam String username){

        return userService.recommendFriend(username);
    }
    @PostMapping("api/deleteNotification")
    @ResponseBody
    public void deleteNotification(@RequestParam long notification_id){
        userService.deleteNotification(notification_id);
    }
}
