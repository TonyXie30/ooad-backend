package com.dormitory.backend.pojo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.List;

@Entity(name = "dormitory")
@Table(schema = "public")
@Schema
public class dormitory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dormitory_id")
    @Schema
    int id;
    @Column
    @Schema
    String location;
    @Column(name = "building_name")
    @Schema
    String buildingName;
    @Column
    @Schema
    Integer floor;
    @Column(name = "house_num")
    @Schema
    String houseNum;
    @ManyToOne
    @JoinColumn(name = "gender",referencedColumnName = "gender")
    @Schema
    Gender gender;
    @ManyToOne
    @JoinColumn(name = "degree",referencedColumnName = "degree")
    @Schema
    Degree degree;
    @Column(name = "booked_num")
    @Schema
    int bookedNum;
    @Column
    @Schema
    int bed;
    @ManyToMany(mappedBy = "bookmark")
    @Schema
    List<user> marked_users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(String houseNum) {
        this.houseNum = houseNum;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
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

    public List<user> getMarked_users() {
        return marked_users;
    }

    public void setMarked_users(List<user> marked_users) {
        this.marked_users = marked_users;
    }

    public int getBookedNum() {
        return bookedNum;
    }

    public void setBookedNum(int bookedNum) {
        this.bookedNum = bookedNum;
    }

    public int getBed() {
        return bed;
    }

    public void setBed(int bed) {
        this.bed = bed;
    }



}
