package com.csquared.bgcloud.bottomsheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;


import androidx.annotation.LayoutRes;

import com.csquared.bgcloud.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BaseBottomSheet {
    private BottomSheetDialog dialog;
    private Context context;
    private View v;

    public BaseBottomSheet(Context context, @LayoutRes int layout) {
        dialog = new BottomSheetDialog(context);
        this.context = context;
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        v = LayoutInflater.from(context).inflate(layout, null);
    }

    public View getView() {
        return v;
    }

    public void setContentView(View v) {
        dialog.setContentView(v);
    }

    protected Context getContext() {
        return context;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

}
