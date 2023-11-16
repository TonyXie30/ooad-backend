package com.dormitory.backend.pojo;

import jakarta.persistence.*;

import java.util.List;


@Entity(name = "users")
@Table(schema = "public")
public class user {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  int id;
  @Column(nullable = false)
  String username;
  @Column
  String password;
  @ManyToMany
  @JoinTable(name = "bookmark")
  private List<dormitory> bookmark;
  @ManyToOne
  @JoinColumn(name = "dormitory_id")
  private dormitory bookedDormitory;
  @ManyToOne
  @JoinColumn(name = "subject_id")
  private subject subject;
  @ManyToMany
  @JoinTable(name = "favour_bedtime")
  private List<timeRange> bedtime;
  @ManyToMany
  @JoinTable(name = "favour_wakeup_time")
  private List<timeRange> wakeupTime;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
