package com.dormitory.backend.pojo;

import jakarta.persistence.*;

@Entity(name = "subject")
@Table(schema = "public")
public class subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    int id;
    @Column
    String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }
}
