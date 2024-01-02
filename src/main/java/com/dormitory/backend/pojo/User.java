package com.dormitory.backend.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.dormitory.backend.converter.DegreeConverter;
import com.dormitory.backend.converter.GenderConverter;
import com.dormitory.backend.converter.SubjectConverter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.List;


@Entity(name = "users")
@Table(schema = "public")
@Schema
public class User {

    @Column(nullable = false,unique = true)
    @Schema
    @ExcelProperty(value = "姓名")
    private String username;
    @ManyToOne
    @JoinColumn(name = "gender",referencedColumnName = "gender")
    @Schema
    @ExcelProperty(value = "性别", converter = GenderConverter.class)
    private Gender gender;
    @ManyToOne
    @JoinColumn(name = "degree",referencedColumnName = "degree")
    @Schema
    @ExcelProperty(value = "学位", converter = DegreeConverter.class)
    private Degree degree;
    @ManyToOne
    @JoinColumn(name = "subject_id",referencedColumnName = "subject_id")
    @Schema
    @ExcelProperty(value = "专业代码", converter = SubjectConverter.class)
    private Subject subject;
    @Column(nullable = false)
    @Schema
    @ExcelProperty(value = "密码")
    private String password;
    @ManyToOne
    @JoinColumn(name = "bedtime",referencedColumnName = "timeslot")
    @Schema
    private TimeRange bedtime;
    @ManyToOne
    @JoinColumn(name = "uptime",referencedColumnName = "timeslot")
    @Schema
    private TimeRange uptime;
    @ManyToMany //@Fetch(FetchMode.JOIN)
    @JoinTable(name = "bookmark")
    @Schema
    private List<Dormitory> bookmark;
    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    @Schema
    private Dormitory bookedDormitory;
    @ManyToOne
    @JoinTable(name = "leader_id")
    @Schema
    @JsonBackReference //防止json打印无限递归
    private User leaderId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Schema
    private int id;
    @Column
    @Schema
    private String photo;

    @Column
    @Schema
    private boolean admin;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public User getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(User leaderId) {
        this.leaderId = leaderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Dormitory> getBookmark() {
        return bookmark;
    }

    public void setBookmark(List<Dormitory> bookmark) {
        this.bookmark = bookmark;
    }

    public Dormitory getBookedDormitory() {
        return bookedDormitory;
    }

    public void setBookedDormitory(Dormitory bookedDormitory) {
        this.bookedDormitory = bookedDormitory;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public TimeRange getBedtime() {
        return bedtime;
    }

    public void setBedtime(TimeRange bedtime) {
        this.bedtime = bedtime;
    }

    public TimeRange getUptime() {
        return uptime;
    }

    public void setUptime(TimeRange uptime) {
        this.uptime = uptime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void insertBookmark(Dormitory dormitory){
        this.bookmark.add(dormitory);
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)){
            throw new IllegalArgumentException("obj is not User");
        }
        return username.equals(((User) obj).getUsername());
    }
}
