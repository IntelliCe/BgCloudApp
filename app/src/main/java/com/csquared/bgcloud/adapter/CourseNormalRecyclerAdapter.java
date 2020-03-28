package com.csquared.bgcloud.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.activity.CourseContentActivity;
import com.csquared.bgcloud.bottomsheet.OutsideBottomSheet;
import com.csquared.bgcloud.json.CourseResponse;
import com.csquared.bgcloud.util.PxHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.List;

public class CourseNormalRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_COURSE_ITEM = 0;
    public static final int VIEW_TYPE_DATE_DIVIDER = 1;

    private Context context;

    public static class CourseItemViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTime, tvCourseName, tvPlatformType;
        final View layoutOutside, layoutDownload, root;
        final MaterialCardView cardView;

        CourseItemViewHolder(View v) {
            super(v);
            tvTime = v.findViewById(R.id.text_course_time);
            tvCourseName = v.findViewById(R.id.text_course_name);
            tvPlatformType = v.findViewById(R.id.text_outside_link);
            layoutDownload = v.findViewById(R.id.layout_download);
            layoutOutside = v.findViewById(R.id.layout_outside);
            root = v.findViewById(R.id.root);
            cardView = v.findViewById(R.id.card_view);
        }
    }

    public static class CourseDateDividerViewHolder extends RecyclerView.ViewHolder {
        final TextView label1, label2;

        CourseDateDividerViewHolder(View v) {
            super(v);
            label1 = v.findViewById(R.id.label1);
            label2 = v.findViewById(R.id.label2);
        }
    }

    private List<CourseResponse.Course> list;

    public CourseNormalRecyclerAdapter(List<CourseResponse.Course> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_COURSE_ITEM) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_course_normal, parent, false);
            return new CourseItemViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_course_normal_date_divider, parent, false);
            return new CourseDateDividerViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CourseResponse.Course course = list.get(position);
        if (holder instanceof CourseDateDividerViewHolder) {
            CourseDateDividerViewHolder h = (CourseDateDividerViewHolder) holder;
            h.label1.setText(course.text1);
            h.label2.setText(course.text2);
        } else if (holder instanceof CourseItemViewHolder) {
            CourseItemViewHolder h = (CourseItemViewHolder) holder;
            h.tvTime.setText(course.courseTime);
            h.tvCourseName.setText(course.courseName);
            if (course.platformType.equals("1")) {
                h.layoutDownload.setVisibility(View.VISIBLE);
                h.layoutOutside.setVisibility(View.INVISIBLE);
            } else if (!course.platformType.equals("")) {
                h.layoutDownload.setVisibility(View.INVISIBLE);
                h.tvPlatformType.setText(course.platformType);
                h.layoutOutside.setVisibility(View.VISIBLE);
            } else {
                h.layoutOutside.setVisibility(View.INVISIBLE);
                h.layoutDownload.setVisibility(View.INVISIBLE);
            }
            if (course.isOpen == 0) {
                h.tvCourseName.setTextColor(Color.parseColor("#808080"));
                h.tvCourseName.setText(course.courseName + "（未开课）");
                h.cardView.setAlpha(0.6f);
            } else {
                h.tvCourseName.setTextColor(Color.parseColor("#000000"));
                h.cardView.setAlpha(1.0f);
            }
            if (position == getItemCount() - 1) {
                ((ViewGroup.MarginLayoutParams) h.root.getLayoutParams())
                        .setMargins(0, 0, 0, PxHelper.px(context, 20));
            } else {
                ((ViewGroup.MarginLayoutParams) h.root.getLayoutParams())
                        .setMargins(0, 0, 0, PxHelper.px(context, 0));
            }

            h.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (course.platformType.equals("1")) {
                        Intent intent = new Intent(context, CourseContentActivity.class);
                        intent.putExtra("course_id", course.lmsCourseNo);
                        intent.putExtra("course_name", course.courseName);
                        context.startActivity(intent);
                    } else if (!course.platformType.equals("")) {
                        OutsideBottomSheet dialog = new OutsideBottomSheet(context, course);
                        dialog.show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isDivider) {
            return VIEW_TYPE_DATE_DIVIDER;
        } else {
            return VIEW_TYPE_COURSE_ITEM;
        }
    }
}
