package com.dormitory.backend.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.dormitory.backend.converter.DegreeConverter;
import com.dormitory.backend.converter.GenderConverter;
import com.dormitory.backend.converter.SubjectConverter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity(name = "users")
@Table(schema = "public")
@Schema
public class user {

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
    private timeRange bedtime;
    @ManyToOne
    @JoinColumn(name = "uptime",referencedColumnName = "timeslot")
    @Schema
    private timeRange uptime;
    @ManyToMany //@Fetch(FetchMode.JOIN)
    @JoinTable(name = "bookmark")
    @Schema
    private List<dormitory> bookmark;
    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    @Schema
    private dormitory bookedDormitory;
    @ManyToOne
    @JoinTable(name = "leader_id")
    @Schema
    @JsonBackReference //防止json打印无限递归
    private user leaderId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Schema
    private int id;

    @ManyToMany
    @JoinTable(name = "exchange_application",
            joinColumns = {@JoinColumn(name = "username",referencedColumnName = "username")},
            inverseJoinColumns = {@JoinColumn(name = "from_username",referencedColumnName = "username")}
    )
    @Schema
    @JsonIgnore
    private Set<user> exchangeApplication;

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

    public user getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(user leaderId) {
        this.leaderId = leaderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<dormitory> getBookmark() {
        return bookmark;
    }

    public void setBookmark(List<dormitory> bookmark) {
        this.bookmark = bookmark;
    }

    public dormitory getBookedDormitory() {
        return bookedDormitory;
    }

    public void setBookedDormitory(dormitory bookedDormitory) {
        this.bookedDormitory = bookedDormitory;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public timeRange getBedtime() {
        return bedtime;
    }

    public void setBedtime(timeRange bedtime) {
        this.bedtime = bedtime;
    }

    public timeRange getUptime() {
        return uptime;
    }

    public void setUptime(timeRange uptime) {
        this.uptime = uptime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void insertBookmark(dormitory dormitory){
        this.bookmark.add(dormitory);
    }

    public Set<user> getExchangeApplication(){
        return exchangeApplication;
    }

    public Set<String> getExchangeApplicationNameList() {
        Set<String> set = new HashSet<>();
        exchangeApplication.forEach(user -> set.add(user.getUsername()));
        return set;
    }


    public void setExchangeApplication(Set<user> exchangeApplication) {
        this.exchangeApplication = exchangeApplication;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof user)){
            throw new IllegalArgumentException("obj is not user");
        }
        return username.equals(((user) obj).getUsername());
    }
}
