package com.dormitory.backend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.*;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;


//@SqlResultSetMapping(
//        name = "SelectionInfoExcelData",
//        entities = {
//                @EntityResult(
//                        entityClass = SelectionInfoExcelData.class,
//                        fields = {
//                                @FieldResult(name = "location",column = "location"),
//                                @FieldResult(name = "buildingName",column = "buildingname"),
//                                @FieldResult(name = "floor",column = "floor"),
//                                @FieldResult(name = "houseNum",column = "housenum"),
//                                @FieldResult(name = "gender",column = "gender"),
//                                @FieldResult(name = "degree",column = "degree"),
//                                @FieldResult(name = "bookedNum",column = "bookednum"),
//                                @FieldResult(name = "bed",column = "bed"),
//                                @FieldResult(name = "userList",column = "userlist")
//                        }
//                )
//        }
//)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectionInfoExcelData {
    @JsonProperty(value = "location")
    String location;
    @JsonProperty(value = "building_name")
    String buildingName;
    @JsonProperty(value = "floor")
    Integer floor;
    @JsonProperty(value = "house_num")
    String houseNum;
    @JsonProperty(value = "gender")
    String gender;
    @JsonProperty(value = "degree")
    String degree;
    @JsonProperty(value = "booked_num")
    int bookedNum;
    @JsonProperty(value = "bed")
    int bed;
    @JsonProperty(value = "user_list")
    String userList;
}
