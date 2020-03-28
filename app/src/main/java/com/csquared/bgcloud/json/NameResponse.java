package com.csquared.bgcloud.json;

public class NameResponse {
    public static final int CODE_SUCCESS = 2000;

    public static class Data {
        public String name;
    }

    public int code;
    public String message;
    public Data data;
}
