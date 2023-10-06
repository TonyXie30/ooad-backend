package com.dormitory.backend.web;

import com.dormitory.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dormitory.backend.pojo.users;

import java.util.Objects;

@RestController
public class LoginController {

  @Autowired
  private UserService userService;

  @CrossOrigin
  @PostMapping(value = "api/login")
  @ResponseBody
  public ResponseEntity<String> login(@RequestBody users requestUser) {

    String username = requestUser.getUsername();
    // 从数据库中查找用户信息
    users user = userService.findByUsername(username);

    // 如果用户不存在或密码不匹配，返回登录失败
    if (user == null || !Objects.equals(requestUser.getPassword(), user.getPassword())) {
      return ResponseEntity.badRequest().body("Login failed");
    } else {
      return ResponseEntity.ok("Login successful");
    }
  }
}

