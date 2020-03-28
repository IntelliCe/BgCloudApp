package com.csquared.bgcloud;

import android.app.Application;

import com.csquared.bgcloud.manager.DownloadManager;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;

public class BGCloudApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DownloadManager.init(getApplicationContext());
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15000)     // set connection timeout.
                        .readTimeout(15000)        // set read timeout.
                ))
                .commit();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DownloadManager.onApplicationTerminated();
    }
}
