package com.csquared.bgcloud.util;

import com.csquared.bgcloud.adapter.CourseTableRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CourseTable {

    public static List<CourseTableRecyclerAdapter.CourseTableHeader> generateCourseTableHeaderList() {
        List<CourseTableRecyclerAdapter.CourseTableHeader> headers = new ArrayList<>();
        headers.add(new CourseTableRecyclerAdapter.CourseTableHeader("第一节", "8:00", "8:50"));
        headers.add(new CourseTableRecyclerAdapter.CourseTableHeader("第二节", "9:00", "9:50"));
        headers.add(new CourseTableRecyclerAdapter.CourseTableHeader("第三节", "10:10", "11:00"));
        headers.add(new CourseTableRecyclerAdapter.CourseTableHeader("第四节", "11:10", "12:00"));
        headers.add(new CourseTableRecyclerAdapter.CourseTableHeader("第五节", "13:30", "14:20"));
        headers.add(new CourseTableRecyclerAdapter.CourseTableHeader("第六节", "14:30", "15:20"));
        headers.add(new CourseTableRecyclerAdapter.CourseTableHeader("第七节", "15:40", "16:30"));
        headers.add(new CourseTableRecyclerAdapter.CourseTableHeader("第八节", "16:10", "17:30"));
        headers.add(new CourseTableRecyclerAdapter.CourseTableHeader("第九节", "18:30", "19:20"));
        headers.add(new CourseTableRecyclerAdapter.CourseTableHeader("第十节", "19:30", "20:20"));
        return headers;
    }
}
