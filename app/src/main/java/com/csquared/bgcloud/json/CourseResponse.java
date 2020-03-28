package com.csquared.bgcloud.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseResponse {
    public static final int CODE_SUCCESS = 2000;

    public static class Week {
        public int week;
        public List<Day> dayList;
    }

    public static class Day {
        public String date;
        public List<Course> courseList;
        public int day;
    }

    public static class Course implements Comparable<Course> {
        public String teacherName;
        public String platformType;
        public int homeworkStatus;
        public String courseUrl;
        public int section;
        public String lmsCourseNo;
        public String courseName;
        public int isOpen;
        public int dayOfWeek;
        public int isLive;
        public String chapterId;
        public String courseNo;
        public int weekNo;
        public String courseSeq;
        @SerializedName("continunity") public int continuance;
        //public String channelId;

        public String courseTime;

        public boolean isDivider;
        public String text1, text2;

        @Override
        public int compareTo(Course c) {
            return Integer.compare(this.section, c.section);
        }
    }

    public int code;
    public String message;
    public List<Week> data;
}
