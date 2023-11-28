package com.dormitory.backend.service;

import com.dormitory.backend.api.CommentRepository;
import com.dormitory.backend.api.DormitoryRepository;
import com.dormitory.backend.api.UserRepository;
import com.dormitory.backend.pojo.dormitory;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.pojo.comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService{

    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    DormitoryRepository dormitoryRepository;
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
    public void teamUp(user user, int leader_id){
        user leader = userRepository.findById(leader_id);
        user.setLeaderId(leader);
        userRepository.save(user);
    }
    public void setComment(comment object,user user, dormitory dormitory, String content, Integer parentId){
        user author = userRepository.findByUsername(user.getUsername());
        object.setUser(author);
        dormitory dormitory1 = dormitoryRepository.findById(dormitory.getId());
        object.setDormitory(dormitory1);
        object.setContent(content);
        if (parentId!=null){
            object.setParent(commentRepository.findById(parentId));
        }
        commentRepository.save(object);
    }
    public List<comment> getComment(dormitory dormitory, Integer parentId){
        return commentRepository.findByDormitoryAndParent(dormitory, commentRepository.findById(parentId));
    }
    public void setBookMark(dormitory dormitory,user user){
        user author = userRepository.findByUsername(user.getUsername());
        author.insertBookmark(dormitory);
        userRepository.save(author);
    }
    public List<dormitory> getBookMark(user user){
        user author = userRepository.findByUsername(user.getUsername());
        return author.getBookmark();
    }
}
