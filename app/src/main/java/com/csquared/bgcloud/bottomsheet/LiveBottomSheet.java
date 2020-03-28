package com.csquared.bgcloud.bottomsheet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.json.CourseContent;
import com.csquared.bgcloud.resolver.DownloadLinkRetriever;
import com.csquared.bgcloud.util.AppUtil;
import com.google.android.material.snackbar.Snackbar;

import java.net.URI;

public class LiveBottomSheet extends BaseBottomSheet {
    private CourseContent.Content content;
    private String versionCode, downloadLink;

    private View btOpen, btCopy;
    private TextView tvLabel;

    public LiveBottomSheet(Context context, CourseContent.Content content) {
        super(context, R.layout.bottom_sheet_towards_live);
        this.content = content;

        onBindViews();
    }

    public void onBindViews() {
        View v = getView();

        btOpen = v.findViewById(R.id.bt_open);
        btCopy = v.findViewById(R.id.bt_copy);
        tvLabel = v.findViewById(R.id.label);

        tvLabel.setText(content.link);

        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(content.link));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                    LiveBottomSheet.this.dismiss();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "无法跳转：不合法的链接。", Toast.LENGTH_SHORT).show();
                    LiveBottomSheet.this.dismiss();
                }
            }
        });
        btCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", content.link);
                cm.setPrimaryClip(mClipData);
                LiveBottomSheet.this.dismiss();
                Toast.makeText(getContext(), "已复制到剪贴板。", Toast.LENGTH_SHORT).show();
            }
        });

        setContentView(v);
    }

}
