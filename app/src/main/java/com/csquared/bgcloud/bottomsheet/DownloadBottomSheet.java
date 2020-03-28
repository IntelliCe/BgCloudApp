package com.csquared.bgcloud.bottomsheet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.dialog.DownloadDialog;
import com.csquared.bgcloud.io.FileIO;
import com.csquared.bgcloud.json.CourseContent;
import com.csquared.bgcloud.manager.DownloadManager;
import com.csquared.bgcloud.net.Downloader;
import com.csquared.bgcloud.resolver.DownloadLinkRetriever;
import com.csquared.bgcloud.util.AppUtil;

import java.io.File;
import java.net.URI;

public class DownloadBottomSheet extends BaseBottomSheet {
    private CourseContent.Content content;
    private BaseBottomSheet parent;
    private String versionCode, downloadLink, courseName, sectionName;

    private View btDownload, btCopy, progressBar, error;
    private TextView tvTitle, tvLabel;
    private ImageView imageView;

    public DownloadBottomSheet(Context context, CourseContent.Content content, String versionCode, BaseBottomSheet parent, String courseName, String sectionName) {
        super(context, R.layout.bottom_sheet_download);
        this.content = content;
        this.parent = parent;
        this.versionCode = versionCode;
        this.courseName = courseName;
        this.sectionName = sectionName;

        onBindViews();
    }

    public void onBindViews() {
        View v = getView();

        btDownload = v.findViewById(R.id.bt_download);
        btCopy = v.findViewById(R.id.bt_copy);
        progressBar = v.findViewById(R.id.progress_bar);
        tvTitle = v.findViewById(R.id.text_title);
        imageView = v.findViewById(R.id.image_view);
        tvLabel = v.findViewById(R.id.label);
        error = v.findViewById(R.id.error);

        tvTitle.setText(content.contentName);
        if (content.type == CourseContent.TYPE_DOCUMENT) {
            imageView.setImageResource(R.drawable.ic_document_black);
        } else if (content.type == CourseContent.TYPE_LIVE) {
            imageView.setImageResource(R.drawable.ic_live_black);
        } else if (content.type == CourseContent.TYPE_VIDEO) {
            imageView.setImageResource(R.drawable.ic_video_black);
        } else {
            imageView.setImageResource(R.drawable.ic_unsupported_black);
        }

        /**
         * download
         */
        btDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadDialog.Callback callback = new DownloadDialog.Callback() {
                    @Override
                    public void onConfirm(String fileName) {
                        String dest = FileIO.getDownloadPath() + "/" + fileName;
                        if (!new File(dest).exists()) {
                            DownloadManager.addTask(downloadLink, dest, courseName, sectionName);
                            Toast.makeText(getContext(), "文件已经开始下载，下拉状态栏即可查看下载进度。", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "该文件已存在。", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                String[] fileArgs = new File(downloadLink).getName().split("\\.");
                String format = fileArgs[fileArgs.length - 1];
                String fileName = courseName + "/" + content.contentName + "." + format;
                dismiss();
                parent.dismiss();
                new DownloadDialog(getContext(), callback, fileName).show();
            }
        });

        DownloadLinkRetriever.Callback callback = new DownloadLinkRetriever.Callback() {
            @Override
            public void onResponse(final String link) {
                AppUtil.findActivity(getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        btDownload.setVisibility(View.VISIBLE);
                        btCopy.setVisibility(View.VISIBLE);
                        tvLabel.setText(link);
                        downloadLink = link;
                    }
                });

                btCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", link);
                        cm.setPrimaryClip(mClipData);
                        DownloadBottomSheet.this.dismiss();
                        Toast.makeText(getContext(), "已复制到剪贴板。", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError() {
                AppUtil.findActivity(getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        error.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        DownloadLinkRetriever retriever = new DownloadLinkRetriever(content.type, versionCode,
                content.chapterId, content.subChapterId, content.serviceId, content.serviceType, callback);
        retriever.start();

        setContentView(v);
    }
}
