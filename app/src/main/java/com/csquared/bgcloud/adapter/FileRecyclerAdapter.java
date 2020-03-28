package com.csquared.bgcloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.bottomsheet.FileBottomSheet;
import com.csquared.bgcloud.io.FileIO;
import com.csquared.bgcloud.manager.DownloadFile;
import com.csquared.bgcloud.manager.DownloadManager;
import com.csquared.bgcloud.util.DateUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

public class FileRecyclerAdapter extends RecyclerView.Adapter<FileRecyclerAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvSource, tvTimeSize;
        final View root;

        ViewHolder(View v) {
            super(v);
            root = v.findViewById(R.id.root);
            tvName = v.findViewById(R.id.text_file_name);
            tvSource = v.findViewById(R.id.text_file_source);
            tvTimeSize = v.findViewById(R.id.text_file_time_size);
        }
    }

    private List<DownloadFile> list;

    public FileRecyclerAdapter(List<DownloadFile> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_file, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final DownloadFile file = list.get(position);
        holder.tvName.setText(new File(file.filePath).getName());
        holder.tvSource.setText("来自 " + file.courseName + "・" + file.sectionName);
        Date date = new Date();
        date.setTime(file.time);
        holder.tvTimeSize.setText(DateUtils.fromTodaySimplified(date) + " @ " + FileIO.getPrintSize(file.size));

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new File(file.filePath).exists()) {
                    new FileBottomSheet(holder.root.getContext(), file).show();
                } else {
                    Toast.makeText(holder.root.getContext(), "此文件已经被手动更名、删除或移动，将在文件列表中移除。", Toast.LENGTH_LONG).show();
                    DownloadManager.removeDownloadedFile(file);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
