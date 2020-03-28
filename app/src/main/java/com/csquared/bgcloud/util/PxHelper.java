package com.csquared.bgcloud.util;

import android.content.Context;

public class PxHelper {
    private float scale;

    public PxHelper(Context context) {
        scale = context.getResources().getDisplayMetrics().density;
    }

    public int px(float dp) {
        return (int) (dp * scale + 0.5f);
    }

    public static int px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
