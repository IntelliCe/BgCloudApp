package com.csquared.bgcloud.json;

public class CurrentDateResponse {
    public static final int CODE_SUCCESS = 2000;

    public static class Data {
        public int theWeek;
        public String today;
    }

    public int code;
    public String message;
    public Data data;
}
