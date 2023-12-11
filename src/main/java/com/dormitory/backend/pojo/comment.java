package com.dormitory.backend.pojo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity(name = "comment")
@Table(schema = "public")
@Schema
public class comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    @Schema
    int id;
    @ManyToOne
    @JoinColumn(referencedColumnName = "comment_id",name = "parent_id")
    @Schema
    comment parent;
    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    @Schema
    dormitory dormitory;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema
    user user;
    @Column
    @Schema
    String content;
    //帮助进行反序列化
    public comment(String content) {
        this.content = content;
    }

    public comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public comment getParent() {
        return parent;
    }

    public void setParent(comment parent) {
        this.parent = parent;
    }

    public com.dormitory.backend.pojo.dormitory getDormitory() {
        return dormitory;
    }

    public void setDormitory(com.dormitory.backend.pojo.dormitory dormitory) {
        this.dormitory = dormitory;
    }

    public com.dormitory.backend.pojo.user getUser() {
        return user;
    }

    public void setUser(com.dormitory.backend.pojo.user user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
