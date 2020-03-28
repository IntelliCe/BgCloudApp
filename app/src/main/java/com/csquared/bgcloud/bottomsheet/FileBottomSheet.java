package com.csquared.bgcloud.bottomsheet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.csquared.bgcloud.BuildConfig;
import com.csquared.bgcloud.R;
import com.csquared.bgcloud.io.FileOpenHelper;
import com.csquared.bgcloud.manager.DownloadFile;
import com.csquared.bgcloud.manager.DownloadManager;

import java.io.File;

public class FileBottomSheet extends BaseBottomSheet {
    private DownloadFile downloadFile;

    private View btOpen, btShare, btDelete;
    private TextView tvTitle;

    public FileBottomSheet(Context context, DownloadFile downloadFile) {
        super(context, R.layout.bottom_sheet_file);
        this.downloadFile = downloadFile;

        onBindViews();
    }

    public void onBindViews() {
        View v = getView();

        btOpen = v.findViewById(R.id.bt_open);
        btShare = v.findViewById(R.id.bt_share);
        btDelete = v.findViewById(R.id.bt_delete);
        tvTitle = v.findViewById(R.id.text_title);

        tvTitle.setText(new File(downloadFile.filePath).getName());

        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                FileOpenHelper.openFile(getContext(), new File(downloadFile.filePath));
            }
        });
        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Uri uriForFile;
                File file = new File(downloadFile.filePath);
                Intent intent = new Intent(Intent.ACTION_SEND);
                if (Build.VERSION.SDK_INT > 23) {
                    uriForFile = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                } else {
                    uriForFile = Uri.fromFile(file);
                }
                intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
                intent.setType("*/*");
                getContext().startActivity(Intent.createChooser(intent, "分享到"));
            }
        });
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(downloadFile.filePath);
                file.delete();
                DownloadManager.removeDownloadedFile(downloadFile);
                dismiss();
            }
        });

        setContentView(v);
    }

}
