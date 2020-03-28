package com.csquared.bgcloud.io;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.csquared.bgcloud.BuildConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import static androidx.core.content.FileProvider.getUriForFile;

public class FileOpenHelper {

    public static void openFile(Context context, File file){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        file);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, getMimeTypeFromFile(file));
                context.startActivity(intent);
            } else {
                Uri uri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, getMimeTypeFromFile(file));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "没有应用可以打开这种类型的文件。", Toast.LENGTH_LONG).show();
        }
    }

    private static String getMimeTypeFromFile(File file) {
        String type = "*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex > 0) {
            String end = fName.substring(dotIndex, fName.length()).toLowerCase(Locale.getDefault());
            HashMap<String, String> map = MyMimeMap.getMimeMap();
            if (!TextUtils.isEmpty(end) && map.keySet().contains(end)) {
                type = map.get(end);
            }
        }
        return type;
    }
}
