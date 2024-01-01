package com.dormitory.backend.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.user;
import com.dormitory.backend.service.CommentService;
import com.dormitory.backend.service.DormitoryService;
import com.dormitory.backend.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class AdminUserController {
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    DormitoryService dormitoryService;
    /**
     * 这个接口接收前端发送的excel，实现批量注册学生账号。
     *
     * @param file excel表格（.xlsx/.xls/.csv）
     */
    @CrossOrigin("http://localhost:8080")
    @PostMapping(path = "api/admin/user/register/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public void createAccountsByExcel(@RequestPart("file") MultipartFile file){
        try{
            System.out.println("upload...");
            // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
            // 具体需要返回多少行可以在`PageReadListener`的构造函数第二项添加指定
//            .read()中，第一个参数输入文件（也支持文件路径），第二个参数指定每行映射到的类，
//            第三个参数内部lambda表达式中完成对数据的操作，其中dataList装载了映射后的对象们。
//            .sheet()中，可指定表页序数或表名来指定读取的文件中的表，默认为第一张。

//            easyExcel实现了分段读取，对内存友好，可在占用少量内存的情况下读取大表格。
            EasyExcel.read(file.getInputStream(), user.class, new PageReadListener<user>(dataList -> {
                for (user newUser:dataList) {
                    if(newUser.getUsername()==null){
//                        没有名字是没有意义的。
                        continue;
                    }
                    if(newUser.getPassword()==null) {
//                        允许密码留空，默认密码123456
                        newUser.setPassword("123456");
                    }
                    if(userService.findByUsernameUnCheck(newUser.getUsername())!=null){
//                        不允许有同一用户名。
                        continue;
                    }
                    newUser.setAdmin(false);
                    userService.register(newUser);
                    userService.teamUp(newUser,newUser);
                }
            })).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @CrossOrigin
    @PostMapping("api/admin/user/deleteUser")
    @ResponseBody
    @Transactional
    public void deleteUser(@RequestParam String username) {
        if (username == null) {
            throw new MyException(Code.MISSING_FIELD);
        }
        user userInDB = userService.findByUsername(username);
        if (userInDB == null) {
            throw new MyException(Code.USER_NOT_EXIST);
        }
        if(userInDB.getBookedDormitory() != null){
            dormitoryService.checkOut(userInDB.getBookedDormitory());
        }
        userService.deleteUser(userInDB);
    }
    @PostMapping(value = "api/admin/deleteComment")
    @Transactional
    @ResponseBody
    public void deleteComment(@RequestParam int comment_id){
        commentService.deleteCommentAndChildrenRecursively(commentService.findById(comment_id));
    }
}
