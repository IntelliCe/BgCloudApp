package com.csquared.bgcloud.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csquared.bgcloud.R;

import java.util.List;

public class CourseTableRecyclerAdapter extends RecyclerView.Adapter<CourseTableRecyclerAdapter.ViewHolder> {

    public static class CourseTableHeader {
        public String name;
        public String startTime, endTime;

        public CourseTableHeader(String name, String startTime, String endTime) {
            this.name = name;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View upperDivider;
        final TextView label;
        ViewHolder(View v) {
            super(v);
            upperDivider = v.findViewById(R.id.divider_upper);
            label = v.findViewById(R.id.label);
        }
    }

    private List<CourseTableHeader> tableHeaders;

    public CourseTableRecyclerAdapter(List<CourseTableHeader> headers) {
        tableHeaders = headers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_course_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseTableHeader header = tableHeaders.get(position);
        holder.label.setText(header.name);
        if (position == 0) {
            holder.upperDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return tableHeaders.size();
    }
}
