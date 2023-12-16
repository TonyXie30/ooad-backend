package com.dormitory.backend.web;

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
    @Autowired
    UserService userService;

    @PostMapping("api/recommend")
    @ResponseBody
    public List<UserProjection> recommendFriend(@RequestParam String username){

        return userService.recommendFriend(username);
    }
}
