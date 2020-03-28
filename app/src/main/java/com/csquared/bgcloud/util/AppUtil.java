package com.csquared.bgcloud.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import java.util.Calendar;
import java.util.Date;

public class AppUtil {

    public static String getGreeting() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >=6 && hour <= 8) {
            return "早上好";
        } else if (hour >= 9 && hour <= 11) {
            return "上午好";
        } else if (hour >= 12 && hour <= 13) {
            return "中午好";
        } else if (hour >= 14 && hour <= 18) {
            return "下午好";
        } else if (hour >= 19 && hour <= 21) {
            return "晚上好";
        } else {
            return "晚安";
        }
    }

    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }
}
