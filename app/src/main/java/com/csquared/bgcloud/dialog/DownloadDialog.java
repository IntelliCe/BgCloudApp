package com.csquared.bgcloud.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.util.AppUtil;
import com.google.android.material.textfield.TextInputEditText;

public class DownloadDialog extends Dialog {
    private Context context;
    private Callback callback;
    private String defaultFileName;

    private Button btNeg, btPos;
    private TextInputEditText etFileName;

    public DownloadDialog(Context context, Callback callback, String defaultFileName) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.callback = callback;
        this.defaultFileName = defaultFileName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download);
        setCanceledOnTouchOutside(false);
        initView();
        refreshView();
        initEvent();

        DisplayMetrics metrics = new DisplayMetrics();
        AppUtil.findActivity(context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getWindow().setLayout((int) (metrics.widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initEvent() {
        btNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.onConfirm(etFileName.getText().toString().trim());
            }
        });
    }

    private void refreshView() {
        etFileName.setText(defaultFileName);
    }

    @Override
    public void show() {
        super.show();
        refreshView();
    }

    private void initView() {
        btPos = findViewById(R.id.bt_pos);
        btNeg = findViewById(R.id.bt_neg);
        etFileName = findViewById(R.id.edit_file_name);
    }

    public interface Callback {
        void onConfirm(String fileName);
    }


}
