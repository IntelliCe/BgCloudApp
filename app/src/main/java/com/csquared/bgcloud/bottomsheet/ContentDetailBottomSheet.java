package com.csquared.bgcloud.bottomsheet;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.adapter.CourseContentDetailRecyclerAdapter;
import com.csquared.bgcloud.json.CourseContent;

public class ContentDetailBottomSheet extends BaseBottomSheet {
    private String courseName, chapterName, courseId;
    private CourseContent.Section section;

    public ContentDetailBottomSheet(Context context, String courseName, String chapterName, CourseContent.Section section, String courseId) {
        super(context, R.layout.bottom_sheet_course_contents);
        this.courseName = courseName;
        this.chapterName = chapterName;
        this.courseName = courseName;
        this.courseId = courseId;
        this.section = section;

        onBindViews();
    }

    public void onBindViews() {
        View v = getView();

        TextView tvTitle1 = v.findViewById(R.id.text_title_1);
        TextView tvTitle2 = v.findViewById(R.id.text_title_2);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        tvTitle1.setText(courseName + "・" + chapterName);
        tvTitle2.setText(section.sectionName);

        CourseContentDetailRecyclerAdapter adapter = new CourseContentDetailRecyclerAdapter(section.contents, getContext(), courseId,
                this, courseName, section.sectionName);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setAdapter(adapter);

        setContentView(v);
    }
}
