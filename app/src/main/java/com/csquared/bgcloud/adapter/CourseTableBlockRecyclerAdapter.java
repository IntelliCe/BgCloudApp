package com.csquared.bgcloud.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.activity.CourseContentActivity;
import com.csquared.bgcloud.bottomsheet.OutsideBottomSheet;
import com.csquared.bgcloud.json.CourseResponse;
import com.csquared.bgcloud.util.PxHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CourseTableBlockRecyclerAdapter extends RecyclerView.Adapter<CourseTableBlockRecyclerAdapter.ViewHolder> {
    private PxHelper pxHelper;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View root, outsideLayout, downloadLayout;
        final MaterialCardView cardView;
        final TextView tvCourseName, tvTeacher, tvOutsideLink;
        ViewHolder(View v) {
            super(v);
            root = v.findViewById(R.id.root);
            outsideLayout = v.findViewById(R.id.layout_outside);
            downloadLayout = v.findViewById(R.id.layout_download);
            cardView = v.findViewById(R.id.card_view);
            tvCourseName = v.findViewById(R.id.text_course_name);
            tvTeacher = v.findViewById(R.id.text_teacher);
            tvOutsideLink = v.findViewById(R.id.text_outside_link);
        }
    }

    private List<CourseResponse.Course> courses;

    public CourseTableBlockRecyclerAdapter(List<CourseResponse.Course> courses, Context context) {
        this.courses = courses;
        this.context = context;
        pxHelper = new PxHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_course_table_block, parent, false);
        return new ViewHolder(view);
    }

    private int lastEndSection = 0;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CourseResponse.Course course = courses.get(position);
        holder.tvCourseName.setText(course.courseName);
        holder.tvTeacher.setText(course.teacherName);
        //TODO: platformType = 1 -> Inner platform
        if (course.platformType != null) {
            holder.tvOutsideLink.setText(course.platformType);
        } else {
            holder.outsideLayout.setVisibility(View.INVISIBLE);
        }

        if (course.platformType.equals("1")) {
            holder.downloadLayout.setVisibility(View.VISIBLE);
            holder.outsideLayout.setVisibility(View.INVISIBLE);
        } else if (!course.platformType.equals("")) {
            holder.downloadLayout.setVisibility(View.INVISIBLE);
            holder.tvOutsideLink.setText(course.platformType);
            holder.tvOutsideLink.setVisibility(View.VISIBLE);
        } else {
            holder.downloadLayout.setVisibility(View.INVISIBLE);
            holder.outsideLayout.setVisibility(View.INVISIBLE);
        }
        if (course.isOpen == 0) {
            holder.tvCourseName.setTextColor(Color.parseColor("#808080"));
            holder.tvCourseName.setText(course.courseName + "（未开课）");
            holder.cardView.setAlpha(0.6f);
        } else {
            holder.tvCourseName.setTextColor(context.getResources().getColor(R.color.colorTheme1, context.getTheme()));
            holder.cardView.setAlpha(1.0f);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
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

        int pxTopMargin = (course.section - lastEndSection - 1) * 50;
        int pxWidth = 42 + (course.continuance - 1) * 50;
        lastEndSection = course.section + course.continuance - 1;
        ((ViewGroup.MarginLayoutParams) holder.root.getLayoutParams())
                .setMargins(pxHelper.px(0), pxHelper.px(pxTopMargin), pxHelper.px(0), pxHelper.px(0));
        holder.cardView.getLayoutParams().height = pxHelper.px(pxWidth);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
