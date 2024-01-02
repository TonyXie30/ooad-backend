package com.dormitory.backend.service;

import com.dormitory.backend.api.*;
import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    SubjectRepository subjectRepository;
    @Autowired
    SelectionTimeConfigRepository selectionTimeConfigRepository;

    public User findByUsername(String username) {
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        User userInDB = userRepository.findByUsername(username);
        if(userInDB==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        return userInDB;
    }
    public User findByUsernameUnCheck(String username) {
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        return userRepository.findByUsername(username);
    }
    public User register(User newUser){
        userRepository.save(newUser);
        return newUser;
    }
    public void bookRoom(User user, Dormitory dorm){
        user.setBookedDormitory(dorm);
        List<User> members = findByLeaderId(user);
        for (User member:members){
            member.setBookedDormitory(dorm);
        }
        userRepository.saveAll(members); //contains leader
    }
    public List<User> findByLeaderId(User user){
        return userRepository.findByLeaderId(user.getLeaderId().getId());
    }
    public List<User> findByLeaderId(String username){
        return userRepository.findByLeaderId(findByUsername(username).getLeaderId().getId());
    }
    public void teamUp(User member, User leader){
        member.setLeaderId(leader);
        userRepository.save(member);
        //系统通知组队信息
        User system = userRepository.findByUsername("System");
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
    public Comment setComment(String username, String dormitoryId, String content, Integer parentId){
        Comment object = new Comment(content);
        User author = userRepository.findByUsername(username);
        object.setUser(author);
        Dormitory dormitory1 = dormitoryRepository.findById(Integer.parseInt(dormitoryId));
        object.setDormitory(dormitory1);
        object.setCreate_time(new Timestamp(System.currentTimeMillis()));
        if (parentId!=null){
            object.setParent(commentRepository.findById(parentId));
        }
        commentRepository.save(object);
        if (parentId==null){
            object.setParent(commentRepository.findById(object.getId()));
        }
        //info the User who add dorm as bookmark
        List<User> receiverGroup = getUsersByBookmarkedDormitoryId(Integer.parseInt(dormitoryId));
        User system = userRepository.findByUsername("System");
        receiverGroup.forEach(user -> communicate(system, user, """
                Notification:
                The dormitory %s in %s Room%s you set as bookmark has new comments. Have a check! If you have\s
                any questions, please communicate with the admin.
                                    
                Sent by System.
                """.formatted(dormitoryId, dormitory1.getBuildingName(), dormitory1.getBookedNum())));
        return object;
    }
    public List<Comment> getComment(Integer dormitoryId, Integer parentId){
        return commentRepository.findByDormitoryAndParent(dormitoryRepository.findById(dormitoryId), commentRepository.findById(parentId));
    }
    public void setBookMark(String dormitoryId,String username){
        User author = userRepository.findByUsername(username);
        Dormitory dorm = dormitoryRepository.findById(Integer.parseInt(dormitoryId));
        author.insertBookmark(dorm);
        userRepository.save(author);
    }
    public List<Dormitory> getBookMark(String username){
        User author = userRepository.findByUsername(username);
        return author.getBookmark();
    }
    public List<Dormitory> getBookMark(User author){
        return author.getBookmark();
    }

    public Page<User> getUsers(Integer page, Integer limit, String sort){
        return getUsers(page,limit,sort, new String[]{"id"});
    }
    public Page<User> getUsers(Integer page, Integer limit, String sort, String[] attr){
        return getUsersBy(page,limit,sort,attr,null);
    }
    public Page<User> getUsers(Integer page, Integer limit, String sort, User hostUser, String[] attr) {
        return getUsersBy(page,limit,sort,attr,hostUser);
    }
    public Page<User> getUsersBy(Integer page, Integer limit, String sort,
                                 String[] attr, User user) {
        Sort sort_ = getSort(sort, attr);
        PageRequest pageable;
        if(page != null && limit != null){
            pageable = PageRequest.of(page,limit,sort_);
        }else {
            pageable = PageRequest.of(0,Integer.MAX_VALUE,sort_);
        }
        if(user==null) return userRepository.findAll(pageable);
        else {
            System.out.println(user.getUsername());
            return userRepository
                    .findPageFilterByUser(user,pageable);
        }
//        if(page != null && limit != null){
//            PageRequest pageable = PageRequest.of(page,limit,sort_);
//
//        }else{
//            if(User==null) return new PageImpl<>(userRepository.findAll(sort_));
//            else {
//                List<User> temp = userRepository
//                        .findFilterByUser(User.getUsername(),User.getGender(),User.getDegree());
//                return new PageImpl<>(temp);
//            }
//        }
    }

    public void deleteUser(User user) {
//        该方法不是队长也可以调用。
        disbandTeam(user);
        notificationRepository.deleteByReceiver(user);
        userRepository.delete(user);
    }

    public TimeRange findTimeSlot(Time time){
        return timeRangeRepository.findByTimeSlot(time);
    }

    public void setUpTime(User userInDB, TimeRange time) {
        userInDB.setUptime(time);
        userRepository.save(userInDB);
    }
    public void setBedTime(User userInDB, TimeRange time) {
        userInDB.setBedtime(time);
        userRepository.save(userInDB);
    }
    public void communicate(User sender, User receiver, String content){
        Notification notification = new Notification();
        notification.setContent(content);
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setTime(new Timestamp(System.currentTimeMillis()));
        notificationRepository.save(notification);
    }
    public List<Notification> checkMailbox(User user){
        return notificationRepository.findByReceiver(user);
    }
    public List<User> getUsersByBookmarkedDormitoryId(int dormitoryId) {
        return userRepository.findByBookmarkedDormitoryId(dormitoryId);
    }
    public void requestTeamUp(User leader, User sender){
        User system = userRepository.findByUsername("System");
        communicate(system, leader, """
                Notification:
                    There's a new application for your team. Please check his profile and decide whether he would join the team.
                    Here's his brief introduction:
                    id: %d,
                    name: %s,
                    gender: %s,
                    subject: %s
                """.formatted(sender.getId(),sender.getUsername(),sender.getGender().toString(),sender.getSubject().getName()));
    }
    public void disbandTeam(User leader) {
        List<User> group_member = userRepository.findByLeaderId(leader.getId());
        leader.setLeaderId(leader);
        userRepository.save(leader);
        group_member.remove(leader);
        User system = userRepository.findByUsername("System");
        for (User member : group_member) {
            member.setLeaderId(member);
            userRepository.save(leader);
            communicate(system, member, """
                    Your team has been disbanded. Please pay attention to that.
                    """);
        }
    }

    public List<UserProjection> recommendFriend(String username){
        User user = userRepository.findByUsername(username);
        if (user==null)
            throw new MyException(Code.USER_NOT_EXIST);
        if (user.getGender()==null||user.getDegree()==null||user.getBedtime()==null||user.getUptime()==null)
            throw new MyException(Code.MISSING_FIELD);
        return userRepository.recommend(user.getGender().toString(),user.getDegree().toString(),
                LocalTime.parse(user.getBedtime().toString()),LocalTime.parse(user.getUptime().toString()),username);
    }

    public boolean checkTimeValid(User user, Timestamp time) {
        SelectionTimeConfig timeConfig = selectionTimeConfigRepository
                .findByGenderAndDegree(user.getGender(),user.getDegree());
        if (timeConfig==null){
            return false;
        }
        return time.after(timeConfig.getStartTime()) & time.before(timeConfig.getEndTime());
    }

    public List<User> getCheckInedUsers() {
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

    public List<User> getRoomCheckInedUsers(Integer dormitoryid) {
        return userRepository.findByCheckInedDormitoryId(dormitoryid);
    }

    public void checkOut(User user) {
        user.setBookedDormitory(null);
        userRepository.save(user);
    }

//    public Set<User> getExchangeApplicationList(String username){
//        return userRepository.findExchangeApplications(username);
//    }
    public void exchangeApply(User sender, User receiver){
        User system = userRepository.findByUsername("System");
        Dormitory dorm = sender.getBookedDormitory();
        communicate(system, receiver, """
                Notification:
                    User %s:
                    There's a new application for room exchange. Please check his profile.
                    Here's the brief introduction of dorm:
                    id: %d,
                    location: %s,
                    buildingName: %s,
                    Room No.: %s
                    Apply from: %s
                """.formatted(receiver.getUsername(),dorm.getId(),dorm.getLocation(),dorm.getBuildingName(),dorm.getHouseNum(),sender.getUsername()));
    }

    public void exchangeAcceptNotification(User user, User fromUser) {
        User system = userRepository.findByUsername("System");
        communicate(system, fromUser, """
                Notification:
                    Your exchange application to user "%s" has been accepted.
                    Your room is changed from: %s -> %s
                """.formatted(user.getUsername(),
                fromUser.getBookedDormitory().getHouseNum(),
                user.getBookedDormitory().getHouseNum()));
        exchangeRoom(user,fromUser);
    }

    public void exchangeRejectNotification(User user, User fromUser) {
        User system = userRepository.findByUsername("System");
        communicate(system, fromUser, """
                Notification:
                    Your exchange application to user "%s" has been accepted.
                    Your room is changed from: %s -> %s
                """.formatted(user.getUsername(),
                fromUser.getBookedDormitory().getHouseNum(),
                user.getBookedDormitory().getHouseNum()));
    }

    public void exchangeRoom(User user1, User user2) {
        Dormitory temp1 = (user1.getBookedDormitory());
        Dormitory temp2 = (user2.getBookedDormitory());
        user1.setBookedDormitory(temp2);
        user2.setBookedDormitory(temp1);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    public User updateUser(User user) {
        userRepository.save(user);
        return userRepository.findByUsername(user.getUsername());
    }

    public Subject getSubject(String subjectId) {
        Subject temp = subjectRepository.findById(subjectId);
        System.out.println(temp.getName());
        return temp;
    }
    public void deleteNotification(long id){
        Notification notification = notificationRepository.findById(id);
        notificationRepository.delete(notification);
    }



//    public void saveExchangeApplicationCache(
//            ConcurrentHashMap<String, Set<String>> exchangeApplicationCache) {
//        exchangeApplicationCache.forEach((usr,fromSet)->{
//            User userInDB = findByUsername(usr);
//            Set<User> fromSetToDB = new HashSet<>();
//            fromSet.forEach(from->{
//                fromSetToDB.add(findByUsername(from));
//            });
//            userInDB.setExchangeApplication(fromSetToDB);
//            userRepository.save(userInDB);
//        });
//    }
}
