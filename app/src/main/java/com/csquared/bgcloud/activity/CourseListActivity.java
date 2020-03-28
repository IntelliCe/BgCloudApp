package com.csquared.bgcloud.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.adapter.CourseNormalRecyclerAdapter;
import com.csquared.bgcloud.adapter.CourseTableRecyclerAdapter;
import com.csquared.bgcloud.json.CourseResponse;
import com.csquared.bgcloud.resolver.BgCloudResolver;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {
    private CourseResponse courseResponse;
    private int theWeek;

    RecyclerView recyclerView;
    TextView tvActivityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        Bundle bundle = getIntent().getExtras();
        courseResponse = new Gson().fromJson(bundle.getString("course_data"), CourseResponse.class);
        Log.d("AAA", "onCreate: " + bundle.getString("course_data"));
        theWeek = bundle.getInt("the_week");

        tvActivityName = findViewById(R.id.text_activity_name);
        recyclerView = findViewById(R.id.recycler_view);

        List<CourseResponse.Course> courseList = BgCloudResolver.getWeekCourseListWithDateDivider(theWeek, courseResponse);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setAdapter(new CourseNormalRecyclerAdapter(courseList, this));

        setRecyclerView();

        findViewById(R.id.bt_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (theWeek < 20) {
                    theWeek++;
                    setRecyclerView();
                }
            }
        });

        findViewById(R.id.bt_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (theWeek > 1) {
                    theWeek--;
                    setRecyclerView();
                }

            }
        });

        // back button
        findViewById(R.id.bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setRecyclerView() {
        List<CourseResponse.Course> courseList = BgCloudResolver.getWeekCourseListWithDateDivider(theWeek, courseResponse);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setAdapter(new CourseNormalRecyclerAdapter(courseList, this));
        tvActivityName.setText("所有课程（第" + theWeek + "周）");
    }
}
