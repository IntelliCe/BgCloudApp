package com.csquared.bgcloud.manager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.net.Downloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadManager {
    private static final String CHANNEL_ID_DOWNLOADING = "channel_downloading";
    private static final String CHANNEL_NAME_DOWNLOADING = "下载";
    private static final String CHANNEL_ID_COMPLETED = "channel_completed";
    private static final String CHANNEL_NAME_COMPLETED = "下载完成";

    private static int nidDownloading = -1;
    private static int nidCompleted = 0;

    private static Context context;
    private static NotificationManager notificationManager;
    private static Downloader downloader;

    private static List<DownloadFile> files;

    public static void init(Context context) {
        DownloadManager.context = context;
        SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String json = pref.getString("downloaded-file-list", null);
        if (json == null) {
            files = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<DownloadFile>>(){}.getType();
            files = new Gson().fromJson(json, type);
        }
        downloader = Downloader.getInstance();

        createNotificationChannels();
    }

    public static void addTask(String url, String pathUri, String courseName, String sectionName) {
        downloader.startDownLoadFileSingle(url, pathUri, courseName, sectionName, getNextNidDownloading());
    }

    public static void updateDownloadingNotification(String fileName, int progress, int nid) {
        NotificationCompat.Builder builderDownloading = new NotificationCompat.Builder(context, CHANNEL_ID_DOWNLOADING)
                .setContentTitle(fileName)
                .setContentText(progress + "%")
                .setSubText("正在下载")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_download_black)
                .setProgress(100, progress, false);
        Notification notification = builderDownloading.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(nid, builderDownloading.build());
    }

    public static void cancelDownloadingNotification(int nid) {
        notificationManager.cancel(nid);
    }

    public static void createCompletedNotification(String fileName) {
        final NotificationCompat.Builder builderCompleted = new NotificationCompat.Builder(context, CHANNEL_ID_COMPLETED)
                .setContentTitle("已完成下载")
                .setContentText(fileName)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_check_circle_black);
        notificationManager.notify(getNextNidCompleted(), builderCompleted.build());
    }

    private static int getNextNidCompleted() {
        nidCompleted++;
        return nidCompleted;
    }

    private static int getNextNidDownloading() {
        nidDownloading--;
        return nidDownloading;
    }

    private static void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel1 =
                    new NotificationChannel(CHANNEL_ID_DOWNLOADING, CHANNEL_NAME_DOWNLOADING,
                            NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setBypassDnd(true);
            channel1.canBypassDnd();
            channel1.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            channel1.setDescription("显示当前的下载进度");
            channel1.setName("下载进度");
            channel1.setShowBadge(true);
            channel1.setSound(null, null);
            channel1.enableVibration(false);
            notificationManager.createNotificationChannel(channel1);

            NotificationChannel channel2 =
                    new NotificationChannel(CHANNEL_ID_COMPLETED, CHANNEL_NAME_COMPLETED,
                            NotificationManager.IMPORTANCE_HIGH);
            channel2.setBypassDnd(true);
            channel2.canBypassDnd();
            //channel2.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            channel2.setDescription("通知下载完成");
            channel2.setName("下载完成");
            channel2.setShowBadge(true);
            channel2.setVibrationPattern(new long[] {100});
            channel2.enableVibration(true);
            notificationManager.createNotificationChannel(channel2);
        }
    }

    public static void addDownloadedFile(String courseName, String sectionName, String path, long size) {
        files.add(0, new DownloadFile(courseName, sectionName, path, size));
        saveStatus();
        onDownloadedFileListChanged();
    }

    private static void saveStatus() {
        SharedPreferences.Editor pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE).edit();
        pref.putString("downloaded-file-list", new Gson().toJson(files)).apply();
    }

    public static List<DownloadFile> getDownloadedFiles() {
        return files;
    }

    public static void removeDownloadedFile(DownloadFile file) {
        files.remove(file);
        saveStatus();
        onDownloadedFileListChanged();
    }

    public static void onApplicationTerminated() {
        context = null;
        notificationManager.cancelAll();
    }

    private static List<DownloadedFileListener> listeners = new ArrayList<>();
    public interface DownloadedFileListener {
        void onDownloadedFileListChanged(List<DownloadFile> files);
    }

    private static void onDownloadedFileListChanged() {
        for (DownloadedFileListener listener : listeners) {
            listener.onDownloadedFileListChanged(files);
        }
    }

    public static void addDownloadedFileListener(DownloadedFileListener listener) {
        listeners.add(listener);
    }
}
