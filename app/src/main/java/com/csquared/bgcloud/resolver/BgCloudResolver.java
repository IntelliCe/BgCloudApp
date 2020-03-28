package com.csquared.bgcloud.resolver;

import com.csquared.bgcloud.json.CourseContent;
import com.csquared.bgcloud.json.CourseResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BgCloudResolver {

    public static List<CourseResponse.Course> getTodayCourseList(int theWeek, String date, CourseResponse response) {
        if (response.code != CourseResponse.CODE_SUCCESS) {
            return null;
        }
        List<CourseResponse.Course> courseList = new ArrayList<>();
        for (CourseResponse.Week week : response.data) {
            if (week.week == theWeek) {
                for (CourseResponse.Day day : week.dayList) {
                    if (day.date.equals(date)) {
                        courseList.addAll(day.courseList);
                    }
                }
            }
        }
        Collections.sort(courseList);
        return courseList;
    }

    private static String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    private static String[] startTime = {"8:00", "9:00", "10:10", "11:10", "13:30", "14:30", "15:40", "16:10", "18:30", "19:30"};
    private static String[] endTime = {"8:50", "9:50", "11:00", "12:00", "14:20", "15:20", "16:30", "17:30", "19:20", "20:20"};
    public static List<CourseResponse.Course> getWeekCourseListWithDateDivider(int theWeek, CourseResponse response) {
        if (response.code != CourseResponse.CODE_SUCCESS) {
            return null;
        }
        List<CourseResponse.Course> courseList = new ArrayList<>();
        for (CourseResponse.Week week : response.data) {
            if (week.week == theWeek) {
                for (CourseResponse.Day day : week.dayList) {
                    // add a date divider
                    CourseResponse.Course divider = new CourseResponse.Course();
                    divider.isDivider = true;
                    divider.text1 = weekDays[day.day - 1];
                    divider.text2 = day.date.substring(0, 4) + "年" + day.date.substring(4, 6) + "月" + day.date.substring(6, 8) + "日";
                    courseList.add(divider);

                    List<CourseResponse.Course> dayCourses = new ArrayList<>(day.courseList);
                    Collections.sort(dayCourses);
                    for (CourseResponse.Course course : dayCourses) {
                        String start = startTime[course.section - 1];
                        String end = endTime[course.section + course.continuance - 2];
                        course.courseTime = start + "-" + end;
                    }
                    courseList.addAll(dayCourses);
                }
            }
        }
        return courseList;
    }

    public static List<CourseContent.Section> normalizeCourseContentAsSection(CourseContent content) {
        List<CourseContent.Section> sections = new ArrayList<>();
        for (CourseContent.Chapter chapter : content.chapters) {
            CourseContent.Section divider = new CourseContent.Section(chapter.chapterName);
            divider.__isListDivider = true;
            sections.add(divider);
            sections.addAll(chapter.sections);
        }
        return sections;
    }

    public static final int WEEKLY_STATS_CLASS = 0;
    public static final int WEEKLY_STATS_HOUR = 1;
    public static final int WEEKLY_STATS_HOMEWORK = 2;
    public static final int WEEKLY_STATS_LIVE = 3;
    public static int[] getWeeklyStats(CourseResponse res, int theWeek) {
        int[] result = new int[4];
        int vClass = 0, vHour = 0, vHomework = 0, vLive = 0;
        for (CourseResponse.Week week : res.data) {
            if (week.week == theWeek) {
                for (CourseResponse.Day day : week.dayList) {
                    for (CourseResponse.Course course : day.courseList) {
                        vClass++;
                        vHour += 50 * course.continuance;
                        if (course.homeworkStatus != 0) {
                            vHomework++;
                        }
                        if (course.isLive == 1) {
                            vLive++;
                        }
                    }
                }
            }
        }
        result[WEEKLY_STATS_CLASS] = vClass;
        result[WEEKLY_STATS_HOUR] = vHour;
        result[WEEKLY_STATS_HOMEWORK] = vHomework;
        result[WEEKLY_STATS_LIVE] = vLive;
        return result;
    }
}
