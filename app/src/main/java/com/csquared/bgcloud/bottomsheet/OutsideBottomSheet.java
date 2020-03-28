package com.csquared.bgcloud.bottomsheet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.json.CourseResponse;
import com.csquared.bgcloud.util.AppIntent;

public class OutsideBottomSheet extends BaseBottomSheet {
    private CourseResponse.Course course;

    private View btLaunch, btOpen, btCopy;
    private TextView tvLabel, tvApp;
    private ImageView ivApp;

    private String courseUrl;

    public OutsideBottomSheet(Context context, CourseResponse.Course course) {
        super(context, R.layout.bottom_sheet_towards_app);
        this.course = course;

        onBindViews();
    }

    public void onBindViews() {
        View v = getView();

        btLaunch = v.findViewById(R.id.bt_launch);
        btOpen = v.findViewById(R.id.bt_open);
        btCopy = v.findViewById(R.id.bt_copy);
        tvLabel = v.findViewById(R.id.label);
        tvApp = v.findViewById(R.id.text_app);
        ivApp = v.findViewById(R.id.image_app);

        if (course.courseUrl != null) {
            courseUrl = course.courseUrl;
        } else {
            courseUrl = "暂时没有提供详细链接";
        }
        tvLabel.setText(courseUrl);

        final AppIntent.App app = AppIntent.getAppIntent(course.platformType);
        if (app != null) {
            ivApp.setImageResource(app.drawable);
            tvApp.setText("打开应用：" + app.name);
        } else {
            ivApp.setImageResource(R.drawable.ic_unsupported_black);
            tvApp.setText("暂不支持打开此应用");
            tvApp.setTextColor(Color.parseColor("#808080"));
            btLaunch.setClickable(false);
        }

        btLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageManager packageManager = getContext().getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage(app.pkg);
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "未安装该应用。",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    OutsideBottomSheet.this.dismiss();
                }
            }
        });
        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(course.courseUrl));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "无法跳转：不合法的链接。", Toast.LENGTH_SHORT).show();
                } finally {
                    OutsideBottomSheet.this.dismiss();
                }
            }
        });
        btCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", courseUrl);
                cm.setPrimaryClip(mClipData);
                OutsideBottomSheet.this.dismiss();
                Toast.makeText(getContext(), "已复制到剪贴板。", Toast.LENGTH_SHORT).show();
            }
        });

        setContentView(v);
    }

}
