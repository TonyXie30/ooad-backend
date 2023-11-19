package com.dormitory.backend.service;

import com.dormitory.backend.api.UserRepository;
import com.dormitory.backend.pojo.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService{

    @Autowired
    UserRepository userRepository;

    public user findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public void register(user newUser){
        userRepository.save(newUser);
    }

}
