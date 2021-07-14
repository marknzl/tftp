package me.marknzl.shared;

import java.util.HashMap;

public enum ErrorCode {

    NOT_DEFINED,
    FILE_NOT_FOUND,
    ACCESS_VIOLATION,
    DISK_FULL,
    ILLEGAL_OP,
    UNKNOWN_TID,
    FILE_ALREADY_EXISTS,
    NO_SUCH_USER;

    public static final HashMap<ErrorCode, Integer> ecToIntMappings = new HashMap<>() {{
       put(NOT_DEFINED, 0);
       put(FILE_NOT_FOUND, 1);
       put(ACCESS_VIOLATION, 2);
       put(DISK_FULL, 3);
       put(ILLEGAL_OP, 4);
       put(UNKNOWN_TID, 5);
       put(FILE_ALREADY_EXISTS, 6);
       put(NO_SUCH_USER, 7);
    }};

    public static ErrorCode[] intToECMappings = new ErrorCode[] { NOT_DEFINED, FILE_NOT_FOUND, ACCESS_VIOLATION, DISK_FULL, ILLEGAL_OP, UNKNOWN_TID, FILE_ALREADY_EXISTS, NO_SUCH_USER };

}
