package com.csquared.bgcloud.json;

public class LoginResponse {
    public static final int CODE_SUCCESS = 2000;

    public static class Data {
        public String token;
    }

    public int code;
    public String message;
    public Data data;
}
