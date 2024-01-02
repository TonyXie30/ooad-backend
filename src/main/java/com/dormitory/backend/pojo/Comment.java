package com.dormitory.backend.pojo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity(name = "comment")
@Table(schema = "public")
@Schema
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    @Schema
    int id;
    @ManyToOne
    @JoinColumn(referencedColumnName = "comment_id",name = "parent_id")
    @Schema
    Comment parent;
    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    @Schema
    Dormitory dormitory;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema
    User user;
    @Column
    @Schema
    String content;
    //帮助进行反序列化
    @Column
    @Schema
    Timestamp create_time;
    public Comment(String content) {
        this.content = content;
    }

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public Dormitory getDormitory() {
        return dormitory;
    }

    public void setDormitory(Dormitory dormitory) {
        this.dormitory = dormitory;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
