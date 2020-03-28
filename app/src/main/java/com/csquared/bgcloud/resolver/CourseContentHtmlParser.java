package com.csquared.bgcloud.resolver;

import android.util.Log;

import com.csquared.bgcloud.json.CourseContent;
import com.csquared.bgcloud.statics.Session;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class CourseContentHtmlParser extends Thread {
    private Callback callback;
    private String courseId, userId;

    public CourseContentHtmlParser(String courseId, String userId, Callback callback) {
        this.callback = callback;
        this.courseId = courseId;
        this.userId = userId;
    }

    public void parse() {
        start();
    }

    public interface Callback {
        void onResponse(CourseContent c);
        void onError();
    }

    @Override
    public void run() {
        try {
            String url = "http://dufestu.edufe.com.cn/student/redirectKczy?userId=" + userId + "&courseId=" + courseId;
            Document doc = Jsoup.connect(url).get();
            CourseContent courseContent = new CourseContent();

            if (!Session.hasCookie()) {
                new LoginCookieGetter(url).get();
            }

            Elements chapterFolders = doc.getElementsByClass("chapter chapter-folded chapter-expand");
            for (Element chapterFolder : chapterFolders) {
                CourseContent.Chapter chapter = new CourseContent.Chapter(
                        chapterFolder.getElementsByTag("h3").first().ownText());
                Elements spans = chapterFolder.getElementsByTag("span");
                Elements courseLists = chapterFolder.getElementsByClass("course-list");
                int index = 0;
                for (Element courseList : courseLists) {
                    CourseContent.Section section = new CourseContent.Section(spans.get(index).ownText());
                    index++;
                    Elements courses = courseList.getElementsByTag("a");
                    for (Element course : courses) {
                        String className = course.attr("class");
                        int type;
                        if (className.startsWith("text ")) {
                            type = CourseContent.TYPE_DOCUMENT;
                        } else if (className.startsWith("video ")) {
                            type = CourseContent.TYPE_VIDEO;
                        } else if (className.startsWith("live ")) {
                            type = CourseContent.TYPE_LIVE;
                        } else {
                            type = CourseContent.TYPE_UNSUPPORTED;
                        }
                        CourseContent.Content content = new CourseContent.Content(type);
                        content.contentName = course.attr("title");
                        if (type == CourseContent.TYPE_LIVE) {
                            content.link = course.attr("href");
                        } else if (type ==  CourseContent.TYPE_VIDEO || type == CourseContent.TYPE_DOCUMENT) {
                            String func = course.attr("onclick");
                            // goStudy(83802
                            // 111542
                            // 66376
                            // 2
                            // '0')
                            String[] args = func.split(",");
                            args[0] = args[0].replace("goStudy(", "");
                            args[4] = args[4].replace("'", "").replace(")", "");
                            content.chapterId = args[0];
                            content.subChapterId = args[1];
                            content.serviceId = args[2];
                            content.serviceType = Integer.parseInt(args[3]);
                            content.__undefined = args[4];
                        }
                        section.contents.add(content);
                    }
                    chapter.sections.add(section);
                }
                courseContent.chapters.add(chapter);
            }
            callback.onResponse(courseContent);

            Log.d("PARSER", "run: n = " + courseContent.chapters.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            callback.onError();
        }
    }
}
