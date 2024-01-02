package com.dormitory.backend.web;

import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.Team;
import com.dormitory.backend.pojo.User;
import com.dormitory.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeamController {
    @Autowired
    private UserService userService;
    @CrossOrigin
    @PostMapping(value = "api/teamUp")
    @ResponseBody
    public User teamUp(@RequestParam String memberName, @RequestParam String leaderName){
        try{
            User member = userService.findByUsername(memberName);
            User leader = userService.findByUsername(leaderName);
            if(leader==null||member==null){
                throw new MyException(Code.USER_NOT_EXIST);
            }
            if(leader.getId()==member.getId()){
                // 已存在的队长。
                // 不视为异常，但也不进行数据库交互。
                System.out.println("existed leader");
                return member;
            }
            if(member.getId()!=member.getLeaderId().getId()){
//                先退队才能进队
                throw new MyException(Code.ALREADY_IN_TEAM);
            }
            if(leader.getId()!=leader.getLeaderId().getId()){
                User realLeader = userService.findByUsername(leader.getLeaderId().getUsername());
                userService.teamUp(member,realLeader);
            }else {
                userService.teamUp(member, leader);
            }
            return member;
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        throw new MyException(Code.METHOD_FAILED);
    }

    @CrossOrigin
    @PostMapping(value = "api/leaveTeam")
    @ResponseBody
    public User leaveTeam(String username){
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }

        User user = userService.findByUsername(username);

        if(user==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        if (user.getId()==user.getLeaderId().getId())
            userService.disbandTeam(user);
        userService.teamUp(user,user);
        return user;
    }

    @CrossOrigin
    @PostMapping(value = "api/disbandTeam")
    @ResponseBody
    public void disbandTeam(String leader_name){
        if (leader_name==null)
            throw new MyException(Code.MISSING_FIELD);
        User leader = userService.findByUsername(leader_name);
        if (leader==null)
            throw new MyException(Code.USER_NOT_EXIST);
        userService.disbandTeam(leader);
    }

    @CrossOrigin
    @PostMapping(value = "api/kickMember")
    @ResponseBody
    public void kickMember(String leader_name,String username){
        if (username==null||leader_name==null)
            throw new MyException(Code.MISSING_FIELD);
        User user = userService.findByUsername(username);
        User leader = userService.findByUsername(leader_name);
        List<User> team = userService.findByLeaderId(leader);
        if (!team.contains(user))
            throw new MyException(Code.NOT_HAVE_THIS_MEMBER);
        else
            userService.kickMember(user);
    }

    @CrossOrigin
    @PostMapping(value = "api/getTeamLeader")
    @ResponseBody
    public User getTeamLeader(String username){
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        User user = userService.findByUsername(username);
        if(user==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        return user.getLeaderId();
    }

    @CrossOrigin
    @PostMapping(value = "api/getTeam")
    @ResponseBody
    public Team getTeam(String username){
        if(username==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        User user = userService.findByUsername(username);
        if(user==null){
            throw new MyException(Code.USER_NOT_EXIST);
        }
        List<User> userList = userService.findByLeaderId(user);
        return new Team(userList,user.getLeaderId(),userList.size());
    }

    @CrossOrigin
    @PostMapping(value = "api/requestTeamUp")
    @ResponseBody
    public void requestTeamUp(String leaderName, String username) {
        if (leaderName == null)
            throw new MyException(Code.MISSING_FIELD);

        User leader = userService.findByUsername(leaderName);
        User sender = userService.findByUsername(username);
        if (leader == null || sender == null)
            throw new MyException(Code.USER_NOT_EXIST);
        if (sender.getId() != sender.getLeaderId().getId()) {
//            先退队才能申请加其他队
            throw new MyException(Code.ALREADY_IN_TEAM);
        }
        if (leader.getId() != leader.getLeaderId().getId()) {
            User realLeader = userService.findByUsername(leader.getLeaderId().getUsername());
            userService.requestTeamUp(realLeader, sender);
        } else {
            userService.requestTeamUp(leader, sender);
        }
    }
}
