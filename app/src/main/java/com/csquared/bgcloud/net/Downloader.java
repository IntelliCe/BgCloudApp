package com.csquared.bgcloud.net;

import android.util.Log;

import com.csquared.bgcloud.manager.DownloadManager;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

public class Downloader {
    public static Downloader instance = null;

    public static Downloader getInstance() {
        if (null == instance) {
            instance = new Downloader();
        }
        return instance;
    }

    /**
     * 单任务下载
     *
     * @param downLoadUri  文件下载网络地址
     * @param destinationUri 下载文件的存储绝对路径
     */
    public void startDownLoadFileSingle(String downLoadUri, String destinationUri, final String courseName, final String sectionName, final int nid) {
        FileDownloader.getImpl()
                .create(downLoadUri)
                .setPath(destinationUri)
                .setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        //Log.d("DOWNLOADER", "progress: " + soFarBytes);
                        DownloadManager.updateDownloadingNotification(task.getFilename(),
                                (int)((float) soFarBytes / totalBytes * 100), nid);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        DownloadManager.cancelDownloadingNotification(nid);
                        DownloadManager.createCompletedNotification(task.getFilename());
                        DownloadManager.addDownloadedFile(courseName, sectionName, task.getPath(), task.getLargeFileTotalBytes());
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {

                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();
    }
}
