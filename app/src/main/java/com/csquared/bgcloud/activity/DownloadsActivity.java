package com.csquared.bgcloud.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.adapter.CourseContentRecyclerAdapter;
import com.csquared.bgcloud.adapter.FileRecyclerAdapter;
import com.csquared.bgcloud.manager.DownloadFile;
import com.csquared.bgcloud.manager.DownloadManager;

import java.util.List;

public class DownloadsActivity extends AppCompatActivity implements DownloadManager.DownloadedFileListener {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setAdapter(new FileRecyclerAdapter(DownloadManager.getDownloadedFiles()));

        DownloadManager.addDownloadedFileListener(this);

        // back button
        findViewById(R.id.bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDownloadedFileListChanged(List<DownloadFile> files) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setAdapter(new FileRecyclerAdapter(DownloadManager.getDownloadedFiles()));
    }
}
