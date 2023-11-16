package com.dormitory.backend.pojo;

import jakarta.persistence.*;

@Entity(name = "comment")
@Table(schema = "public")
public class comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    int id;
    @ManyToOne
    @JoinColumn(referencedColumnName = "comment_id",name = "parent_id")
    comment parent;
    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    dormitory dormitory;
    @ManyToOne
    @JoinColumn(name = "user_id")
    user user;
    @Column
    String content;


}
