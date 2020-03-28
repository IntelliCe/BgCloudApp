package com.csquared.bgcloud.statics;

public class Session {
    public static String studentId;
    public static String token;
    public static String cookie;

    public static boolean hasCookie() {
        return cookie != null;
    }
}
