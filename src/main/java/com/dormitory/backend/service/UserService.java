package com.dormitory.backend.service;

import com.dormitory.backend.api.UserRepository;
import com.dormitory.backend.pojo.dormitory;
import com.dormitory.backend.pojo.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService{

    @Autowired
    UserRepository userRepository;

    public user findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public user register(user newUser){
        userRepository.save(newUser);
        return newUser;
    }
    public void bookRoom(user user, dormitory dorm){
        user.setBookedDormitory(dorm);
        List<user> members = userRepository.findByLeaderId(user.getLeaderId().getId());
        userRepository.saveAll(members); //contains leader
    }

}
