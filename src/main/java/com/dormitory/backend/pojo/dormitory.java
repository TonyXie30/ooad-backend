package com.dormitory.backend.pojo;

import jakarta.persistence.*;

import java.util.List;

@Entity(name = "dormitory")
@Table(schema = "public")
public class dormitory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dormitory_id")
    int id;
    @Column
    String location;
    @Column(name = "building_name")
    String buildingName;
    @Column
    int floor;
    @Column(name = "house_num")
    String houseNum;
    @Column
    String type;
    @Column(name = "booked_num")
    int bookedNum;
    @Column
    String bed;
    @ManyToMany(mappedBy = "bookmark")
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBookedNum() {
        return bookedNum;
    }

    public void setBookedNum(int bookedNum) {
        this.bookedNum = bookedNum;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }



}
