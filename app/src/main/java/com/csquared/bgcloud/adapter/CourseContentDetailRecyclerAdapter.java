package com.csquared.bgcloud.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.bottomsheet.BaseBottomSheet;
import com.csquared.bgcloud.bottomsheet.DownloadBottomSheet;
import com.csquared.bgcloud.bottomsheet.LiveBottomSheet;
import com.csquared.bgcloud.json.CourseContent;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class CourseContentDetailRecyclerAdapter extends RecyclerView.Adapter<CourseContentDetailRecyclerAdapter.ViewHolder> {
    private Context context;
    private BaseBottomSheet parent;
    private String courseId, courseName, sectionName;

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View root;
        final ImageView imageView;
        final TextView label;
        ViewHolder(View v) {
            super(v);
            root = v.findViewById(R.id.root);
            imageView = v.findViewById(R.id.image_view);
            label = v.findViewById(R.id.label);
        }
    }

    private List<CourseContent.Content> contents;

    public CourseContentDetailRecyclerAdapter(List<CourseContent.Content> contents, Context context, String courseId,
                                              BaseBottomSheet parent, String courseName, String sectionName) {
        this.contents = contents;
        this.context = context;
        this.courseId = courseId;
        this.parent = parent;
        this.courseName = courseName;
        this.sectionName = sectionName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_course_content_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CourseContent.Content content = contents.get(position);
        if (content.type == CourseContent.TYPE_DOCUMENT) {
            holder.imageView.setImageResource(R.drawable.ic_document_black);
        } else if (content.type == CourseContent.TYPE_LIVE) {
            holder.imageView.setImageResource(R.drawable.ic_live_black);
        } else if (content.type == CourseContent.TYPE_VIDEO) {
            holder.imageView.setImageResource(R.drawable.ic_video_black);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_unsupported_black);
        }
        holder.label.setText(content.contentName);

        if (content.type == CourseContent.TYPE_DOCUMENT || content.type == CourseContent.TYPE_VIDEO) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadBottomSheet dialog = new DownloadBottomSheet(context, content, courseId, parent, courseName, sectionName);
                    dialog.show();
                }
            });
        } else if (content.type == CourseContent.TYPE_LIVE) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LiveBottomSheet dialog = new LiveBottomSheet(context, content);
                    dialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }
}
