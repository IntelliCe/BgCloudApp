package com.csquared.bgcloud.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.activity.CourseContentActivity;
import com.csquared.bgcloud.bottomsheet.FileBottomSheet;
import com.csquared.bgcloud.bottomsheet.OutsideBottomSheet;
import com.csquared.bgcloud.json.CourseResponse;
import com.csquared.bgcloud.manager.DownloadFile;
import com.csquared.bgcloud.manager.DownloadManager;
import com.csquared.bgcloud.util.DateUtils;
import com.csquared.bgcloud.util.PxHelper;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecentFilesRecyclerAdapter extends RecyclerView.Adapter<RecentFilesRecyclerAdapter.FileItemViewHolder> {
    private Context context;

    public static class FileItemViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvSource, tvTime;
        final MaterialCardView cardView;

        FileItemViewHolder(View v) {
            super(v);
            tvTime = v.findViewById(R.id.text_time);
            tvName = v.findViewById(R.id.text_file_name);
            tvSource = v.findViewById(R.id.text_file_source);
            cardView = v.findViewById(R.id.card_view);
        }
    }

    private List<DownloadFile> list;

    public RecentFilesRecyclerAdapter(List<DownloadFile> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public FileItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_recent_file, parent, false);
        return new FileItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FileItemViewHolder holder, int position) {
        final DownloadFile file = list.get(position);
        holder.tvName.setText(new File(file.filePath).getName());
        holder.tvSource.setText(file.courseName + "・" + file.sectionName);
        Date date = new Date();
        date.setTime(file.time);
        holder.tvTime.setText(DateUtils.fromTodaySimplified(date));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new File(file.filePath).exists()) {
                    new FileBottomSheet(holder.cardView.getContext(), file).show();
                } else {
                    Toast.makeText(holder.cardView.getContext(), "此文件已经被手动更名、删除或移动，将在文件列表中移除。", Toast.LENGTH_LONG).show();
                    DownloadManager.removeDownloadedFile(file);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(list.size(), 3);
    }
}
