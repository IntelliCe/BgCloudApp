package com.csquared.bgcloud.json;

import java.util.ArrayList;
import java.util.List;

public class CourseContent {
    public static final int TYPE_LIVE = 0;
    public static final int TYPE_DOCUMENT = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_UNSUPPORTED = 9;

    public static class Chapter {
        public String chapterName;
        public List<Section> sections;

        public Chapter(String name) {
            chapterName = name;
            sections = new ArrayList<>();
        }
    }

    public static class Section {
        public String sectionName;
        public List<Content> contents;

        public boolean __isListDivider = false;

        public Section(String name) {
            sectionName = name;
            contents = new ArrayList<>();
        }

        public Section() {
            contents = new ArrayList<>();
        }
    }

    public static class Content {
        public int type;
        public String contentName;
        public String link;

        // goStudy(83802,111552,51143,4,'849')
        public String chapterId;
        public String subChapterId;
        public String serviceId;
        public int serviceType;
        public String __undefined;

        public Content(int type) {
            this.type = type;
        }
    }

    public List<Chapter> chapters;

    public CourseContent() {
        chapters = new ArrayList<>();
    }

}
