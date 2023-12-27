package com.dormitory.backend.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "Gender")
@Table(schema = "public")
@Schema
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Gender {

    @Id
    @Column(unique = true)
    @Schema
    String gender;

    public Gender(String gender) {
        this.gender = gender;
    }
    public Gender(){}

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return gender;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return this.gender.equals(((Gender) obj).getGender());
    }
}
