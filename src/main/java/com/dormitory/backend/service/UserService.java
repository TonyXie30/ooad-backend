package com.dormitory.backend.service;

import com.dormitory.backend.api.*;
import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
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
    @Autowired
    SelectionTimeConfigRepository selectionTimeConfigRepository;

    public user findByUsername(String username) {
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        user userInDB = userRepository.findByUsername(username);
        if(userInDB==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        return userInDB;
    }
    public user findByUsernameUnCheck(String username) {
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        return userRepository.findByUsername(username);
    }
    public user register(user newUser){
        userRepository.save(newUser);
        return newUser;
    }
    public void bookRoom(user user, dormitory dorm){
        user.setBookedDormitory(dorm);
        List<user> members = findByLeaderId(user);
        userRepository.saveAll(members); //contains leader
    }
    public List<user> findByLeaderId(user user){
        return userRepository.findByLeaderId(user.getLeaderId());
    }
    public List<user> findByLeaderId(String username){
        return userRepository.findByLeaderId(findByUsername(username).getLeaderId());
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
    public comment setComment(String username, String dormitoryId, String content, Integer parentId){
        comment object = new comment(content);
        user author = userRepository.findByUsername(username);
        object.setUser(author);
        dormitory dormitory1 = dormitoryRepository.findById(Integer.parseInt(dormitoryId));
        object.setDormitory(dormitory1);
        object.setCreate_time(new Timestamp(System.currentTimeMillis()));
        if (parentId!=null){
            object.setParent(commentRepository.findById(parentId));
        }
        commentRepository.save(object);
        if (parentId==null){
            object.setParent(commentRepository.findById(object.getId()));
        }
        //info the user who add dorm as bookmark
        List<user> receiverGroup = getUsersByBookmarkedDormitoryId(Integer.parseInt(dormitoryId));
        user system = userRepository.findByUsername("System");
        receiverGroup.forEach(user -> communicate(system, user, """
                Notification:
                The dormitory %s in %s Room%s you set as bookmark has new comments. Have a check! If you have\s
                any questions, please communicate with the admin.
                                    
                Sent by System.
                """.formatted(dormitoryId, dormitory1.getBuildingName(), dormitory1.getBookedNum())));
        return object;
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
    public List<dormitory> getBookMark(user author){
        return author.getBookmark();
    }

    public Page<user> getUsers(Integer page,Integer limit,String sort){
        return getUsers(page,limit,sort, new String[]{"id"});
        // 调用 JpaRepository 的 findAll 方法，传入 Specification 对象
    }
    public Page<user> getUsers(Integer page,Integer limit,String sort,String[] attr){
        return getUsersBy(page,limit,sort,attr,null);
    }
    public Page<user> getUsersBy(Integer page, Integer limit, String sort,
                                 String[] attr, String by) {
        Sort sort_ = getSort(sort, attr);
        if(page != null && limit != null){
            PageRequest pageable = PageRequest.of(page,limit,sort_);
            if(by==null)return userRepository.findAll(pageable);
//            根据某一属性提取对应
//            return userRepository.getBy(by,pageable);
        }else{
            if(by==null)return new PageImpl<>(userRepository.findAll(sort_));
        }
        return null;
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

    public void disbandTeam(user leader) {
        List<user> group_member = userRepository.findByLeaderId(leader);
        leader.setLeaderId(leader);
        userRepository.save(leader);
        group_member.remove(leader);
        user system = userRepository.findByUsername("System");
        for (user member : group_member) {
            member.setLeaderId(member);
            userRepository.save(leader);
            communicate(system, member, """
                    Your team has been disbanded. Please pay attention to that.
                    """);
        }
    }

    public Gender getGender(String gender){
        return genderRepository.findByGender(gender);
    }
    public Degree getDegree(String degree){
        return degreeRepository.findByDegree(degree);
    }
    public List<UserProjection> recommendFriend(String username){
        user user = userRepository.findByUsername(username);
        if (user==null)
            throw new MyException(Code.USER_NOT_EXIST);
        if (user.getGender()==null||user.getDegree()==null||user.getBedtime()==null||user.getUptime()==null)
            throw new MyException(Code.MISSING_FIELD);
        return userRepository.recommend(user.getGender().toString(),user.getDegree().toString(),
                LocalTime.parse(user.getBedtime().toString()),LocalTime.parse(user.getUptime().toString()),username);
    }

    public boolean checkTimeValid(user user,Timestamp time) {
        SelectionTimeConfig timeConfig = selectionTimeConfigRepository
                .findByGenderAndDegree(user.getGender(),user.getDegree());
        if (timeConfig==null){
            throw new  MyException(Code.TIME_CONFIG_NOT_EXIST);
        }
        return time.after(timeConfig.getStartTime()) & time.before(timeConfig.getEndTime());
    }

    public List<user> getCheckInedUsers() {
        return userRepository.getByBookedDormitoryIsNotNull();
    }

    private static Sort getSort(String sort, String[] attr) {
        Sort sort_;
        if (sort ==null|| sort.equals("+")){
            sort_ = Sort.by(attr).ascending();
        } else {
            sort_ = Sort.by(attr).descending();
        }
        return sort_;
    }

    public List<user> getRoomCheckInedUsers(Integer dormitoryid) {
        return userRepository.findByCheckInedDormitoryId(dormitoryid);
    }

    public void checkOut(user user) {
        user.setBookedDormitory(null);
        userRepository.save(user);
    }
}
