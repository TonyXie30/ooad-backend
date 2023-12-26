package com.dormitory.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.dormitory.backend.utils.ReadFile.readSQLFile;


@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public void timeRangeInitialization() {
        String sql = readSQLFile(new File("src/main/resources/time_slots.sql"));
        jdbcTemplate.execute(sql);
        System.out.println("time_range initialization success");
    }
    public void genderInitialization() {
        String sql = readSQLFile(new File("src/main/resources/gender.sql"));
        jdbcTemplate.execute(sql);
        System.out.println("gender initialization success");
    }
    public void degreeInitialization() {
        String sql = readSQLFile(new File("src/main/resources/degree.sql"));
        jdbcTemplate.execute(sql);
        System.out.println("degree initialization success");
    }

    public void subjectInitialization() {
        String sql = readSQLFile(new File("src/main/resources/subject.sql"));
        jdbcTemplate.execute(sql);
        System.out.println("subject initialization success");
    }

    public void systemInitialization(){
        String sql = "INSERT INTO users(admin,password,username) " +
                "VALUES (true,'system','System') ON CONFLICT(username) do nothing ;";
        jdbcTemplate.execute(sql);
        System.out.println("system user initialization success");
    }
    @Override
    public void run(ApplicationArguments args) {
        timeRangeInitialization();
        genderInitialization();
        degreeInitialization();
        subjectInitialization();
        systemInitialization();
    }
}
