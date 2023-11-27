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
    REG_BAD_PASSWORD_FORMAT(5101,"wrong password format, should be characters and/or numbers"),
    REG_EXISTED_USER(5102, "Register failed: existed user"),
    LOGIN_FAILED(5103, "Login failed, check username or password"),
    Room_Occupied(5104, "Room not available"),
    UNAUTHORISED_NOT_LEADER(5105,"Unauthorised: user should be a team leader");

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
