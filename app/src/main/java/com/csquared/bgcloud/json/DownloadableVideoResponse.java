package com.csquared.bgcloud.json;

import java.util.List;

public class DownloadableVideoResponse {

    public static class Source {
        public String file;
    }

    public String title;
    public String vid;
    public List<Source> sources;
    public String image;
}
