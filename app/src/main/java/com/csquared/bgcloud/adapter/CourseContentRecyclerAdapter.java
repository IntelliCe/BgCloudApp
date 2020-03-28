package com.csquared.bgcloud.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.bottomsheet.ContentDetailBottomSheet;
import com.csquared.bgcloud.json.CourseContent;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class CourseContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_CONTENT_ITEM = 0;
    public static final int VIEW_TYPE_CHAPTER_DIVIDER = 1;

    private Context context;
    private String courseName, courseId;

    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        final TextView tvSectionName;
        final View root;

        ContentViewHolder(View v) {
            super(v);
            tvSectionName = v.findViewById(R.id.text_section_name);
            root = v.findViewById(R.id.root);
        }
    }

    public static class ChapterDividerViewHolder extends RecyclerView.ViewHolder {
        final TextView label;

        ChapterDividerViewHolder(View v) {
            super(v);
            label = v.findViewById(R.id.label);
        }
    }

    List<CourseContent.Section> courseSections;

    public CourseContentRecyclerAdapter(List<CourseContent.Section> courseSections, String courseName, String courseId, Context context) {
        this.courseSections = courseSections;
        this.context = context;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_CONTENT_ITEM) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_course_content, parent, false);
            return new ContentViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_course_content_chapter_divider, parent, false);
            return new ChapterDividerViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final CourseContent.Section section = courseSections.get(position);
        if (holder instanceof ContentViewHolder) {
            ContentViewHolder h = (ContentViewHolder) holder;
            h.tvSectionName.setText(section.sectionName);

            h.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentDetailBottomSheet dialog =
                            new ContentDetailBottomSheet(context, courseName, getChapterName(position), section, courseId);
                    dialog.show();
                }
            });


        } else if (holder instanceof ChapterDividerViewHolder) {
            ChapterDividerViewHolder h = (ChapterDividerViewHolder) holder;
            h.label.setText(section.sectionName);
        }
    }

    @Override
    public int getItemCount() {
        return courseSections.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (courseSections.get(position).__isListDivider) {
            return VIEW_TYPE_CHAPTER_DIVIDER;
        } else {
            return VIEW_TYPE_CONTENT_ITEM;
        }
    }

    private String getChapterName(int position) {
        for (int i = position; i >= 0; i--) {
            if (courseSections.get(i).__isListDivider) {
                return courseSections.get(i).sectionName;
            }
        }
        return "";
    }
}
