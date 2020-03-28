package com.csquared.bgcloud.io;

import android.util.Log;

import java.io.File;

public class FileIO {
    private static final String DOWNLOAD_PATH = "/Download/BgCloud";

    private static String root = null;
    private static File path = null;

    public static void init(File f) {
        root = f.getAbsolutePath();
        path = new File(root + DOWNLOAD_PATH);
        Log.d("IO", "setRoot: " + FileIO.root);
    }

    public static void createDownloadFolder() {
        if (!path.exists()) {
            Log.d("IO", "createSrcFolder: " + path.getAbsolutePath());

            if (path.mkdirs()) {
                Log.d("IO", "createSrcFolder: folder created.");
            } else {
                Log.e("IO", "createSrcFolder: folder not created.");
            }
        }
    }

    public static boolean isReady() {
        return path != null;
    }

    public static File getDownloadFolderFile() {
        return path;
    }

    public static String getDownloadPath() {
        return root + DOWNLOAD_PATH;
    }

    public static String getPrintSize(long size) {
        long rest = 0;
        if(size < 1024){
            return String.valueOf(size) + "B";
        }else{
            size /= 1024;
        }

        if(size < 1024){
            return String.valueOf(size) + "KB";
        }else{
            rest = size % 1024;
            size /= 1024;
        }

        if(size < 1024){
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((rest * 100 / 1024 % 100)) + "MB";
        }else{
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
    }
}
