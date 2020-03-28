package com.csquared.bgcloud.manager;

public class DownloadFile implements Comparable<DownloadFile> {
    public String courseName;
    public String sectionName;
    public String filePath;
    public long size;
    public long time;

    public DownloadFile(String courseName, String sectionName, String filePath, long size) {
        this.courseName = courseName;
        this.sectionName = sectionName;
        this.filePath = filePath;
        this.size = size;
        this.time = System.currentTimeMillis();
    }

    @Override
    public int compareTo(DownloadFile f) {
        return -Long.compare(time, f.time);
    }
}
