package com.dormitory.backend.config;

/**
 * Define all used return code and corresponding message.<br>
 * Whenever need to return a code to front-end, it is suggested to define clearly here first and use as Code.XXX.
 * Especially error code.
 */
public enum Code {
//    generally, 5xxx means something goes wrong.
    SUCCESS(0,"success"),// try not throw an exception, with success message
    MISSING_FIELD(5000, "Missing required field"),
    METHOD_FAILED(5001, "method failed for unknown reason"),
    REG_BAD_PASSWORD_FORMAT(5101,"wrong password format, should be characters and/or numbers"),
    REG_EXISTED_USER(5102, "Register failed: existed user"),
    LOGIN_FAILED(5103, "Login failed, check username or password"),
    Room_Occupied(5104, "Room not available"),
    UNAUTHORISED_NOT_LEADER(5105,"Unauthorised operation: user should be a team leader"),
    UNAUTHORISED_NOT_ADMIN(5106,"Unauthorised operation: user should be an administrator"),
    USER_NOT_EXIST(5107,"Cannot find this user"),
    DORMITORY_NOT_EXIST(5108,"Cannot find this dormitory"),
    TIME_NOT_EXIST(5109, "This time slot is not existed, check if inserted yet."),
    GENERAL_NOT_EXIST(5110, "At least one attribute referring to not exist in database"),
    NOT_HAVE_THIS_MEMBER(5111, "The team don't have this member"),
    TIME_CONFIG_NOT_EXIST(5112, "Corresponding time configuration not exist in database");

    private final int code;
    private final String msg;

    Code(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
