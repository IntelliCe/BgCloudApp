package com.csquared.bgcloud.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.adapter.CourseTableBlockRecyclerAdapter;
import com.csquared.bgcloud.adapter.CourseTableRecyclerAdapter;
import com.csquared.bgcloud.adapter.IntTypeAdapter;
import com.csquared.bgcloud.adapter.RecentFilesRecyclerAdapter;
import com.csquared.bgcloud.io.FileIO;
import com.csquared.bgcloud.json.CourseResponse;
import com.csquared.bgcloud.json.CurrentDateResponse;
import com.csquared.bgcloud.json.NameResponse;
import com.csquared.bgcloud.manager.DownloadFile;
import com.csquared.bgcloud.manager.DownloadManager;
import com.csquared.bgcloud.net.OkHttpBuilder;
import com.csquared.bgcloud.resolver.BgCloudResolver;
import com.csquared.bgcloud.statics.Session;
import com.csquared.bgcloud.util.AppUtil;
import com.csquared.bgcloud.util.CourseTable;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liulishuo.filedownloader.FileDownloader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements DownloadManager.DownloadedFileListener {

    View root, scrollView, layoutEmptyFile;
    MaterialButton btAllCourses, btAllFiles;
    TextView tvGreeting, tvDate, tvWClass, tvWHour, tvWHomework, tvWLive;
    RecyclerView rvToday, rvTodayBody, rvFiles;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.root);
        scrollView = findViewById(R.id.scroll_view);
        layoutEmptyFile = findViewById(R.id.layout_file_empty);
        btAllCourses = findViewById(R.id.bt_all_courses);
        btAllFiles = findViewById(R.id.bt_all_files);
        tvGreeting = findViewById(R.id.text_greeting);
        tvDate = findViewById(R.id.text_date);
        rvToday = findViewById(R.id.recycler_today);
        rvTodayBody = findViewById(R.id.recycler_today_body);
        rvFiles = findViewById(R.id.recycler_recent_file);
        progressBar = findViewById(R.id.progress_bar);

        tvWClass = findViewById(R.id.stat_classes);
        tvWHour = findViewById(R.id.stat_hr);
        tvWHomework = findViewById(R.id.stat_homework);
        tvWLive = findViewById(R.id.stat_live);

        retrieveAllInfoFromDUFE();
        List<CourseTableRecyclerAdapter.CourseTableHeader> courseTableHeaders
                = CourseTable.generateCourseTableHeaderList();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvToday.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvToday.setAdapter(new CourseTableRecyclerAdapter(courseTableHeaders));

        scrollView.setVisibility(View.INVISIBLE);

        DownloadManager.addDownloadedFileListener(this);

        btAllFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void retrieveAllInfoFromDUFE() {
        getCurrentDateFromDUFE();
        getNameFromDUFE(Session.token);
        getCourseListFromDUFE(Session.token);
    }

    private int callbackReturnCount = 0;
    private String currentDateData, nameData, courseData;
    private void handleCallbacks(int i, String data) {
        Log.d("DUFE", "handleCallbacks: data = " + data);
        callbackReturnCount++;
        Log.d("DUFE", "handleCallbacks: callbackReturnCount = " + callbackReturnCount);
        if (i == 0) currentDateData = data;
        if (i == 1) nameData = data;
        if (i == 2) courseData = data;
        if (callbackReturnCount == 3) {
            resolveResponse();
        }
    }

    private void resolveResponse() {
        // check validity
        if (currentDateData == null || nameData == null || courseData == null) {
            Snackbar.make(root, "获取信息失败，请稍后再试。", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // resolve CurrentDateResponse
        final CurrentDateResponse dResponse = new Gson().fromJson(currentDateData, CurrentDateResponse.class);
        if (dResponse.code != CurrentDateResponse.CODE_SUCCESS) {
            Snackbar.make(root, "请求出错：getCurrentDate()。", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String[] date = dResponse.data.today.split("-");
        final StringBuilder builder = new StringBuilder();
        builder.append(date[0]).append("年").append(date[1]).append("月").append(date[2]).append("日")
                .append("  第").append(dResponse.data.theWeek).append("周");

        // resolve NameResponse
        final NameResponse nResponse = new Gson().fromJson(nameData, NameResponse.class);
        if (nResponse.code != NameResponse.CODE_SUCCESS) {
            Snackbar.make(root, "请求出错：getName()。", Snackbar.LENGTH_SHORT).show();
            return;
        }
        setLoginNameRecord(nResponse.data.name);

        // resolve CourseResponse
        Gson customGson = new GsonBuilder()
                .registerTypeAdapter(int.class, new IntTypeAdapter())
                .registerTypeAdapter(Integer.class, new IntTypeAdapter()).create();
        final CourseResponse cResponse = customGson.fromJson(courseData, CourseResponse.class);

        final int[] weeklyStats = BgCloudResolver.getWeeklyStats(cResponse, dResponse.data.theWeek);

        // ui thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("UI", "run: ");
                tvDate.setText(builder.toString());
                tvGreeting.setText(String.format(Locale.getDefault(), "%s，%s", AppUtil.getGreeting(), nResponse.data.name));
                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                rvTodayBody.setLayoutManager(layoutManager);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                rvTodayBody.setAdapter(new CourseTableBlockRecyclerAdapter(
                        BgCloudResolver.getTodayCourseList(dResponse.data.theWeek, dResponse.data.today.replace("-", ""), cResponse), MainActivity.this));

                btAllCourses.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, CourseListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("course_data", new Gson().toJson(cResponse));
                        bundle.putInt("the_week", dResponse.data.theWeek);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                tvWClass.setText(String.format(Locale.getDefault(), "%d", weeklyStats[BgCloudResolver.WEEKLY_STATS_CLASS]));
                tvWHour.setText(String.format(Locale.getDefault(), "%d", weeklyStats[BgCloudResolver.WEEKLY_STATS_HOUR]));
                tvWHomework.setText(String.format(Locale.getDefault(), "%d", weeklyStats[BgCloudResolver.WEEKLY_STATS_HOMEWORK]));
                tvWLive.setText(String.format(Locale.getDefault(), "%d", weeklyStats[BgCloudResolver.WEEKLY_STATS_LIVE]));

                progressBar.setVisibility(View.INVISIBLE);
                scrollView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getCurrentDateFromDUFE() {
        OkHttpClient client = new OkHttpBuilder().create(this);
        Request request = new Request.Builder()
                .url("http://dufestu.edufe.com.cn/student/currentDate")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handleCallbacks(0, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                handleCallbacks(0, response.body().string());
            }
        });
    }

    private void getNameFromDUFE(String token) {
        OkHttpClient client = new OkHttpBuilder().create(this);
        Request request = new Request.Builder()
                .method("POST", new FormBody.Builder().build())
                .url("http://dufestu.edufe.com.cn/student/getName")
                .addHeader("token", token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handleCallbacks(1, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                handleCallbacks(1, response.body().string());
            }
        });
    }

    private void getCourseListFromDUFE(String token) {
        OkHttpClient client = new OkHttpBuilder().create(this);
        Request request = new Request.Builder()
                .url("http://dufestu.edufe.com.cn/student/queryCourseList")
                .addHeader("token", token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handleCallbacks(2, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                handleCallbacks(2, response.body().string());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecentFileView(DownloadManager.getDownloadedFiles());
    }

    private void refreshRecentFileView(List<DownloadFile> list) {
        if (list.isEmpty()) {
            layoutEmptyFile.setVisibility(View.VISIBLE);
            rvFiles.setVisibility(View.GONE);
        } else {
            layoutEmptyFile.setVisibility(View.GONE);
            rvFiles.setVisibility(View.VISIBLE);
            RecentFilesRecyclerAdapter adapter = new RecentFilesRecyclerAdapter(list, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvFiles.setLayoutManager(layoutManager);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            rvFiles.setAdapter(adapter);
        }
    }

    @Override
    public void onDownloadedFileListChanged(List<DownloadFile> files) {
        refreshRecentFileView(files);
    }

    private void setLoginNameRecord(String name) {
        SharedPreferences.Editor pref = getSharedPreferences("auth", Activity.MODE_PRIVATE).edit();
        pref.putString("name", name).apply();
    }

    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出应用。", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
