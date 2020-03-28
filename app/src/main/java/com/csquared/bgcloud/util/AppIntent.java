package com.csquared.bgcloud.util;

import androidx.annotation.DrawableRes;

import com.csquared.bgcloud.R;

import java.util.ArrayList;
import java.util.List;

public class AppIntent {
    private static List<App> list;

    static {
        list = new ArrayList<>();
        list.add(new App("Zoom", R.drawable.pic_app_zoom, "us.zoom.videomeetings"));
        list.add(new App("中国大学慕课平台", R.drawable.pic_app_mooc, "com.netease.edu.ucmooc"));
        list.add(new App("超星学习通", R.drawable.pic_app_cx, "com.chaoxing.mobile"));
    }

    public static class App {
        public String name;
        @DrawableRes public int drawable;
        public String pkg;

        App(String name, @DrawableRes int drawable, String pkg) {
            this.name = name;
            this.drawable = drawable;
            this.pkg = pkg;
        }
    }

    public static App getAppIntent(String name) {
        for (App app : list) {
            if (name.equals(app.name)) {
                return app;
            }
        }
        return null;
    }
}
