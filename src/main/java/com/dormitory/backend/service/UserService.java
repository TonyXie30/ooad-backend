package com.dormitory.backend.service;

import com.dormitory.backend.api.*;
import com.dormitory.backend.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Service
public class UserService{

    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    DormitoryRepository dormitoryRepository;
    @Autowired
    TimeRangeRepository timeRangeRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    DegreeRepository degreeRepository;
    @Autowired
    GenderRepository genderRepository;

    public user findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public user register(user newUser){
        userRepository.save(newUser);
        return newUser;
    }
    public void bookRoom(user user, dormitory dorm){
        user.setBookedDormitory(dorm);
        List<user> members = userRepository.findByLeaderId(user.getLeaderId());
        userRepository.saveAll(members); //contains leader
    }
    public void teamUp(user member, user leader){
        member.setLeaderId(leader);
        userRepository.save(member);
        //系统通知组队信息
        user system = userRepository.findByUsername("System");
        communicate(system,member, """
                Notification:
                    You have teamed up with the leader %s, whose id=%d. Please pay attention to that. If you have\s
                    any questions, please communicate with the admin.
                    
                    Sent by System.
                """.formatted(leader.getUsername(),leader.getId()));
        communicate(system,leader, """
                Notification:
                    You have teamed up with the member %s, whose id=%d. Please pay attention to that. If you have\s
                    any questions, please communicate with the admin.
                    
                    Sent by System.
                """.formatted(member.getUsername(),member.getId()));
    }
    public void setComment(String username, String dormitoryId, String content, Integer parentId){
        comment object = new comment(content);
        user author = userRepository.findByUsername(username);
        object.setUser(author);
        dormitory dormitory1 = dormitoryRepository.findById(Integer.parseInt(dormitoryId));
        object.setDormitory(dormitory1);
        //object.setContent(content);
        if (parentId!=null){
            object.setParent(commentRepository.findById(parentId));
        }
        commentRepository.save(object);
        //info the user who add dorm as bookmark
        List<user> receiverGroup = getUsersByBookmarkedDormitoryId(Integer.parseInt(dormitoryId));
        user system = userRepository.findByUsername("System");
        receiverGroup.forEach(user -> communicate(system, user, """
                Notification:
                The dormitory %s in %s Room%s you set as bookmark has new comments. Have a check! If you have\s
                any questions, please communicate with the admin.
                                    
                Sent by System.
                """.formatted(dormitoryId, dormitory1.getBuildingName(), dormitory1.getBookedNum())));
    }
    public List<comment> getComment(Integer dormitoryId, Integer parentId){
        return commentRepository.findByDormitoryAndParent(dormitoryRepository.findById(dormitoryId), commentRepository.findById(parentId));
    }
    public void setBookMark(String dormitoryId,String username){
        user author = userRepository.findByUsername(username);
        dormitory dorm = dormitoryRepository.findById(Integer.parseInt(dormitoryId));
        author.insertBookmark(dorm);
        userRepository.save(author);
    }
    public List<dormitory> getBookMark(String username){
        user author = userRepository.findByUsername(username);
        return author.getBookmark();
    }

    public Page<user> getUsers(Integer page,Integer limit,String sort){
        if(page != null && limit != null){
            Sort sort_;
            if (sort==null||sort.equals("+")){
                sort_ = Sort.by("id").ascending();
            } else {
                sort_ = Sort.by("id").descending();
            }

            PageRequest pageable = PageRequest.of(page,limit,sort_);
            return userRepository.findAll(pageable);
        }else{
            return new PageImpl<>(userRepository.findAll());
        }
        // 调用 JpaRepository 的 findAll 方法，传入 Specification 对象
    }
    public Page<user> getUsers(Integer page,Integer limit,String sort,String[] attr){
        Sort sort_;
        if (sort==null||sort.equals("+")){
            sort_ = Sort.by(attr).ascending();
        } else {
            sort_ = Sort.by(attr).descending();
        }
        if(page != null && limit != null){
            PageRequest pageable = PageRequest.of(page,limit,sort_);
            return userRepository.findAll(pageable);
        }else{
            return new PageImpl<>(userRepository.findAll(sort_));
        }
    }

    public void deleteUser(user user) {
        userRepository.delete(user);
    }

    public timeRange findTimeSlot(Time time){
        return timeRangeRepository.findByTimeSlot(time);
    }

    public void setUpTime(user userInDB, timeRange time) {
        userInDB.setUptime(time);
        userRepository.save(userInDB);
    }
    public void setBedTime(user userInDB, timeRange time) {
        userInDB.setBedtime(time);
        userRepository.save(userInDB);
    }
    public void communicate(user sender,user receiver,String content){
        Notification notification = new Notification();
        notification.setContent(content);
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setTime(new Timestamp(System.currentTimeMillis()));
        notificationRepository.save(notification);
    }
    public List<Notification> checkMailbox(user user){
        return notificationRepository.findByReceiver(user);
    }
    public List<user> getUsersByBookmarkedDormitoryId(int dormitoryId) {
        return userRepository.findByBookmarkedDormitoryId(dormitoryId);
    }
    public Gender getGender(String gender){
        return genderRepository.findByGender(gender);
    }
    public Degree getDegree(String degree){
        return degreeRepository.findByDegree(degree);
    }
}
