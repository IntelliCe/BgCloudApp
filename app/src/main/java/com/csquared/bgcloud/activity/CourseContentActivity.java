package com.csquared.bgcloud.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.adapter.CourseContentRecyclerAdapter;
import com.csquared.bgcloud.adapter.CourseNormalRecyclerAdapter;
import com.csquared.bgcloud.json.CourseContent;
import com.csquared.bgcloud.resolver.BgCloudResolver;
import com.csquared.bgcloud.resolver.CourseContentHtmlParser;
import com.csquared.bgcloud.statics.Session;

import java.util.List;

public class CourseContentActivity extends AppCompatActivity {
    private String courseId, courseName;

    RecyclerView recyclerView;
    TextView tvActivityName;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_content);
        courseId = getIntent().getStringExtra("course_id");
        courseName = getIntent().getStringExtra("course_name");

        recyclerView = findViewById(R.id.recycler_view);
        tvActivityName = findViewById(R.id.text_activity_name);
        progressBar = findViewById(R.id.progress_bar);

        tvActivityName.setText(courseName);

        getContents(courseId, Session.studentId);

        // back button
        findViewById(R.id.bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getContents(String courseId, String userId) {
        new CourseContentHtmlParser(courseId, userId, new CourseContentHtmlParser.Callback() {
            @Override
            public void onResponse(CourseContent c) {
                handleParsedContents(c);
            }

            @Override
            public void onError() {

            }
        }).parse();
    }

    private void handleParsedContents(final CourseContent c) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                List<CourseContent.Section> courseSections = BgCloudResolver.normalizeCourseContentAsSection(c);
                LinearLayoutManager layoutManager = new LinearLayoutManager(CourseContentActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setAdapter(new CourseContentRecyclerAdapter(courseSections, courseName, courseId, CourseContentActivity.this));
            }
        });
    }

}
