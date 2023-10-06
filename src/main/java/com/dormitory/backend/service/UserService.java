package com.dormitory.backend.service;

import com.dormitory.backend.api.UserRepository;
import com.dormitory.backend.pojo.users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService{

  @Autowired
  UserRepository userRepository;

  public users findByUsername(String username) {
    return userRepository.findByUsername(username);
  }
}
